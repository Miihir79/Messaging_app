package com.example.cameraapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class Splashscreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()
        //line to delete action bar also the notif bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        //lines to add animation to the splash screen
        val top_anim = AnimationUtils.loadAnimation(this,R.anim.top_anim)
       // val bottom_anim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim)

        val welcome= findViewById<TextView>(R.id.Welcome)
        val message= findViewById<TextView>(R.id.message)
        val android = findViewById<ImageView>(R.id.imageView4)
        welcome.startAnimation(top_anim)
        message.startAnimation(top_anim)
        android.startAnimation(top_anim)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // if user has signed in then directly to the main home page of the app otherwise to sign in methods
        Handler().postDelayed({
            if(user != null){
                val intent = Intent(this, Homepage::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this,signin_optios::class.java)
                startActivity(intent)
                finish()
            }

        },1500)
    }
}