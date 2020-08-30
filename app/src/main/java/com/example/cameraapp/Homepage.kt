package com.example.cameraapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_homepage.*

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val intent= intent
        val recivedemail=intent.getStringExtra("email")
        textView9.text=recivedemail

    }
}