package com.example.cameraapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraapp.R
import com.example.cameraapp.new_message_act
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception

class Homepage : AppCompatActivity(), GestureOverlayView.OnGesturePerformedListener {
    private lateinit var gLibrary : GestureLibrary

    companion object{
        private const val CAMERA_PERMISSION =1
        private const val CAMERA =2
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        gestureSetup()

        val intentRecieved = intent
        val recivedemail = intentRecieved.getStringExtra("email")
        val top_anim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        textView10.startAnimation(top_anim)
        textView9.text = recivedemail
        CoroutineScope(Dispatchers.IO).launch {

            val id = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$id")
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    MainScope().launch {
                        val name = snapshot.value
                        textView9.text = name.toString()
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }



        button.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        buttoncam.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION
                )
            }
        }
        signout.setOnClickListener {
            alertDialog()

        }
        imageButton.setOnClickListener {
            val intent = Intent(this, Message::class.java)
            startActivity(intent)
        }

    }

    private fun alertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SIGN OUT")
        builder.setMessage("Are you sure you want to sign out???")
        builder.setIcon(android.R.drawable.ic_lock_power_off)

        builder.setPositiveButton("Yes"){ dialougeInterface,which->
            Toast.makeText(applicationContext,"Signing out",Toast.LENGTH_LONG).show()
            dialougeInterface.dismiss()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, signin_optios::class.java)
            startActivity(intent)
            finish()

        }
        builder.setNegativeButton("No"){dilouge,which->
            Toast.makeText(applicationContext,"Good! you're not leaving",Toast.LENGTH_LONG).show()
            dilouge.dismiss()
        }
        builder.setNeutralButton("Obviously not"){dilouge,which->
            Toast.makeText(applicationContext,"Ha Ha Haa Knew it",Toast.LENGTH_LONG).show()
            dilouge.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CAMERA_PERMISSION){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)

            }else{
                Toast.makeText(this,"Permission denied!",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                imageView.visibility = View.VISIBLE
                imageView.setImageBitmap(thumbnail)
            }
        }
    }

    private fun gestureSetup(){
        try {
            gLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture_new)
        }catch (e:Exception){
            Log.i("TAG", "onCreate: Did not reach here")
            e.printStackTrace()
        }
        try{
            gLibrary.load()
        }catch (e:Exception){
            Log.i("TAG", "onCreate: ${e.message} ${e.cause}")
        }

        gesture_home.addOnGesturePerformedListener(this)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onGesturePerformed(p0: GestureOverlayView?, p1: Gesture?) {
        val predictions = gLibrary.recognize(p1)
        predictions?.let {
            if(it.size > 0 && it[0].score > 1.0){
                val intent = Intent(this@Homepage, new_message_act::class.java)
                startActivity(intent)

            }
        }
    }

}