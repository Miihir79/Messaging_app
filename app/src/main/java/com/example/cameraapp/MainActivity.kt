package com.example.cameraapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.editTextTextPass
import kotlinx.android.synthetic.main.signin.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var color:Int=R.color.weak
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        auth= Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val passwordStrengthCalculator = PasswordCalculator()
        editTextTextPass.addTextChangedListener(passwordStrengthCalculator)


        signup.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            if (editTextTextEmail.text.toString()
                    .isNullOrEmpty() || editTextTextPass.text.toString().isNullOrEmpty() || editTextTextPasscon.text.toString().isNullOrEmpty()
            )
                textView.text = "Not entered Email or Password!"
            else {
                if (editTextTextPasscon.text.toString() != editTextTextPass.text.toString())
                    textView.text = "Passwords do not match"
                else {
                    auth.createUserWithEmailAndPassword(
                        editTextTextEmail.text.toString(),
                        editTextTextPass.text.toString()
                    ).addOnCompleteListener(this)
                    { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                        // task->
                                        //  if(task.isSuccessful){
                                        val user = auth.currentUser
                                        textView.text =
                                            "Successfully created account and sent email verification link"

                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)

                                    }
                                }


                        } else {
                            textView.text = "Sign up failed"
                            updateUI(null)
                        }
                    }

                }

            }

        }





        passwordStrengthCalculator.strengthlevel.observe(this, Observer {strengthlevel->
            displaystrengthlevel(strengthlevel)
        })
        passwordStrengthCalculator.strengthColor.observe(this, Observer { strengthcolor->
            color= strengthcolor
        })
        passwordStrengthCalculator.lowercase.observe(this, Observer {value->
            displaypasswordsugg(value,textView5)
        })

        passwordStrengthCalculator.uppercase.observe(this, Observer {value->
            displaypasswordsugg(value,textView6)

        })

        passwordStrengthCalculator.digit.observe(this, Observer {value->
            displaypasswordsugg(value,textView7)

        })

        passwordStrengthCalculator.special.observe(this, Observer {value->
            displaypasswordsugg(value,textView8)

        })


        textView2.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish()
        }




        }
    private fun updateUI(user: FirebaseUser?){}
    private fun displaypasswordsugg(value:Int,text: TextView){
        if(value==1){
            text.setTextColor(ContextCompat.getColor(this,R.color.bullet))
        }
        else{
            text.setTextColor(ContextCompat.getColor(this,R.color.weak))
        }
    }
    private fun displaystrengthlevel(strengthlevel:String){
        signup.isEnabled = strengthlevel.contains("MEDIUM")|| strengthlevel.contains("STRONG")|| strengthlevel.contains("BULLET")


        textView.text=strengthlevel
        textView.setTextColor(ContextCompat.getColor(this,color))

    }
}



