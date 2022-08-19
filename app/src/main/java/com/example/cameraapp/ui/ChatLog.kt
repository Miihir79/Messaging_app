package com.example.cameraapp.ui

import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.cameraapp.R
import com.example.cameraapp.data.Userdata
import com.example.cameraapp.data.chatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row_2.view.*
import java.util.*

class Chatlog : AppCompatActivity(), TextToSpeech.OnInitListener,GestureOverlayView.OnGesturePerformedListener{

    private var gLibrary : GestureLibrary? = null
    private  var tts : TextToSpeech? = null
    val adapter = GroupAdapter<GroupieViewHolder>()
    private var integerList: MutableList<Int>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        gesturesetup()
        tts = TextToSpeech(this,this)
        //val username = intent.getStringExtra(new_message_act.USER_KEY)
        val user = intent.getParcelableExtra<Userdata>(NewMessageActivity.USER_KEY)
        integerList?.add(0)
        txt_ChatLogTitle.text = user?.username

       // val adapter = GroupAdapter<GroupieViewHolder>()
       // adapter.add(Chatitemto("Hey what's poping"))
       // adapter.add(Chatitemfrom("Hey, nothhing much, same old crap, you say...."))
        Recycler_chat.adapter = adapter

        img_btnBack.setOnClickListener {
            finish()
        }

        if (user != null) {
            loadMess(user)
        }
        send_button.setOnClickListener {
            sendMessage()
            message.text.clear()
        }

        img_speaker.setOnClickListener {
            speakOut(lastmess)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String){
        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
    var lastmess= ""
    var name = ""

    private fun loadMess(userdata: Userdata){

        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object :ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage=snapshot.getValue(chatMessage::class.java)

                if(chatMessage!= null){
                    if(chatMessage.fromid == FirebaseAuth.getInstance().uid && chatMessage.toid == userdata.uid){
                        adapter.add(ChatItemFrom(chatMessage.text))

                    }
                    else if(chatMessage.toid == FirebaseAuth.getInstance().uid && chatMessage.fromid == userdata.uid){
                        lastmess=chatMessage.text
                        name = userdata.username
                        adapter.add(ChatItemTo(chatMessage.text))
                    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(){
        val text= message.text.toString()

        if(text.isNotBlank()){
            val fromId = FirebaseAuth.getInstance().uid
            val user=intent.getParcelableExtra<Userdata>(NewMessageActivity.USER_KEY)
            val toId = user?.uid
            val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
            //val time = LocalTime.now()
            val chatMess = chatMessage(ref.key!!,text,fromId!!, toId!!,System.currentTimeMillis()/1000)
            if(chatMess != null){
                ref.setValue(chatMess).addOnSuccessListener {
                    Log.d("abc","message sent")
                }

            }
            val latestMessRef = FirebaseDatabase.getInstance().getReference("/latest-mess/$fromId/$toId")
            latestMessRef.setValue(chatMess)

            val latestMessToRef = FirebaseDatabase.getInstance().getReference("/latest-mess/$toId/$fromId")
            latestMessToRef.setValue(chatMess)
        }

    }

    override fun onInit(p0: Int) {

        if(p0 == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(applicationContext,"Sorry not able to read that",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(applicationContext,"Initialization failed",Toast.LENGTH_LONG).show()
        }

    }

    public override fun onDestroy() {
        if(tts!=null){

            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
    private fun gesturesetup(){
        gLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture_new)

        if(gLibrary?.load() == false){
            Log.i("TAG", "gesturesetup: error in gesture loading")
        }
        gesture.addOnGesturePerformedListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onGesturePerformed(p0: GestureOverlayView?, p1: Gesture?) {
        val predictions = gLibrary?.recognize(p1)
        predictions?.let {
            if(it.size > 0 && it[0].score > 1.0){
               // Toast.makeText(applicationContext,"hmm",Toast.LENGTH_LONG).show()
               speakOut(lastmess)
            }
        }
    }
}
class ChatItemTo(val text : String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView17.text = text
    }

}
class ChatItemFrom(val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_row_2
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView18.text = text
    }
}