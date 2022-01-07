package com.example.cameraapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
//@RequiresApi(Build.VERSION_CODES.O)
//val abc = LocalTime.now()

class chatMessage(val id:String ,val text: String,val fromid:String,val toid:String,val time:Long){
    constructor(): this("","","","",0)
}