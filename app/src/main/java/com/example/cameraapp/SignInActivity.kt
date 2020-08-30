package com.example.cameraapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.signin.*

class SignInActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth
        setContentView(R.layout.signin)

        SignIn.setOnClickListener {
            val inputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
            if(editTextTextmail.text.toString().isNullOrEmpty() || editTextTextPass.text.toString().isNullOrEmpty())
                textView3.text="Email or password not provided"
            else{
                auth.signInWithEmailAndPassword(editTextTextmail.text.toString(),editTextTextPass.text.toString()).addOnCompleteListener(this){
                    task->
                    if(task.isSuccessful){
                        textView3.text="Signin sucessfull"
                        val user = auth.currentUser
                        updateUI(user,editTextTextmail.text.toString())
                    }
                    else
                        textView3.text="Invalid Email or Password"
                }
            }
        }
        textView4.setOnClickListener {
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)

    }
        textViewforgot.setOnClickListener {
            val intent = Intent(this,Passwordreset::class.java)
            startActivity(intent)
        }
    }
    private  fun updateUI(currentUser :FirebaseUser?,emailAdd:String){
        if(currentUser!=null)
        {
            if(currentUser.isEmailVerified){
                val intent=Intent(this,Homepage::class.java)
                intent.putExtra("email",emailAdd);
                startActivity(intent)

            }
            else{
                Toast.makeText(this,"Email is not verified!",Toast.LENGTH_LONG).show()
            }

        }

    }

    //public override fun onStart() {
  //      super.onStart()
   //     val currentUser = auth.currentUser
   //     updateUI(currentUser,currentUser?.email.toString())
  //  }
}