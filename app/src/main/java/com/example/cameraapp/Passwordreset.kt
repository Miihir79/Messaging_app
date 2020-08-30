package com.example.cameraapp

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.forgotpass.*

class Passwordreset:AppCompatActivity(){
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth= Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpass)

        buttonReset.setOnClickListener{
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
            if(editTextTextReset.text.toString().isNullOrEmpty())
                textView11.text="Email address not provided!"
            else{
                auth.sendPasswordResetEmail(editTextTextReset.text.toString()).addOnCompleteListener(this){
                    task->
                    if(task.isSuccessful){
                        val user=auth.currentUser
                        textView11.text="Resend link is sent"

                    }
                    else
                        textView11.text="Password reset link could not be sent"


                }

            }



        }
    }
}