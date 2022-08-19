package com.example.cameraapp.ui

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraapp.R
import kotlinx.android.synthetic.main.activity_image_view.*
import java.lang.Exception

class ImageView : AppCompatActivity() {
    companion object{
        private const val Storage =1
    }
    private lateinit var bitmap:Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        supportActionBar?.hide()
        bitmap = intent.extras?.get("bitmap") as Bitmap
        zoomImage.setImageBitmap(bitmap)
        DownloadButton.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,  android.Manifest.permission.WRITE_EXTERNAL_STORAGE )==PackageManager.PERMISSION_GRANTED){
                downloadImage(bitmap)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Storage)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Storage){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                downloadImage(bitmap)

            }else{
                Toast.makeText(this,"Permission denied!", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun downloadImage(bitmap: Bitmap){
        try {
            val fos = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME,bitmap.toString())
                    put(MediaStore.Images.Media.MIME_TYPE,"image/png")
                }
            )?.let { contentResolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos)
            Toast.makeText(this,"Image Downloaded",Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}