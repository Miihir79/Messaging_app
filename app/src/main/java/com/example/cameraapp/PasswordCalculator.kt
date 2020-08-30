package com.example.cameraapp

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordCalculator:TextWatcher{

    var strengthlevel: MutableLiveData<String> = MutableLiveData()
    var strengthColor: MutableLiveData<Int> = MutableLiveData()

    var lowercase:MutableLiveData<Int> = MutableLiveData()
    var uppercase:MutableLiveData<Int> = MutableLiveData()
    var digit:MutableLiveData<Int> = MutableLiveData()
    var special:MutableLiveData<Int> = MutableLiveData()

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if(char!=null)
        {
            lowercase.value=if(char.hasLower()){1} else{0}
            uppercase.value=if(char.hasupper()){1} else{0}
            digit.value=if(char.hasdigit()){1} else{0}
            special.value=if(char.hasspecial()){1} else{0}
            calculateStrength(char)
        }


    }
    private fun calculateStrength(password:CharSequence){
        if(password.length in 0..7){
            strengthColor.value=R.color.weak
            strengthlevel.value="WEAK"
        }
        else if(password.length in 8..10 && (lowercase.value==1 || uppercase.value==1 || digit.value==1 || special.value==1)){
            strengthColor.value=R.color.medium
            strengthlevel.value="MEDIUM"
        }
        else if(password.length in 11..16 &&(lowercase.value==1 || uppercase.value==1|| digit.value==1 || special.value==1)){
            strengthlevel.value="STRONG"
            strengthColor.value=R.color.strong
        }
        else if(password.length >16 && lowercase.value==1 || uppercase.value==1|| digit.value==1|| special.value==1){
            strengthColor.value=R.color.bullet
            strengthlevel.value="BULLET PROOF"
        }

    }

    override fun afterTextChanged(p0: Editable?) {}

    private fun CharSequence.hasLower():Boolean{
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLower: Matcher =pattern.matcher(this)
        return hasLower.find()
    }

    private fun CharSequence.hasupper():Boolean{
        val pattern:Pattern=Pattern.compile("[A-Z]")
        val hasupper: Matcher =pattern.matcher(this)
        return hasupper.find()
    }

    private fun CharSequence.hasdigit():Boolean{
        val pattern:Pattern=Pattern.compile("[0-9]")
        val hasdigit: Matcher =pattern.matcher(this)
        return hasdigit.find()
    }

    private fun CharSequence.hasspecial():Boolean{
        val pattern:Pattern=Pattern.compile("[!@#$%^&*()_+={}/;:'<>?/\\[\\]\\,\\~`-]")
        val hasspecial: Matcher =pattern.matcher(this)
        return hasspecial.find()
    }

}