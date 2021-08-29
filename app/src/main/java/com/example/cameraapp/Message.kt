package com.example.cameraapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraapp.new_message_act.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.messagesent.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        recycler_mess.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,Chatlog::class.java)
            val row = item as LatestMessages
            intent.putExtra(USER_KEY,row.chatpartid)
            startActivity(intent)
        }
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this,signin_optios::class.java)
            startActivity(intent)
        }
        new_message.setOnClickListener {
            val intent = Intent(this,new_message_act::class.java)
            startActivity(intent)
        }

        imageButton3.setOnClickListener {
            val intent = Intent(this,Homepage::class.java)
            startActivity(intent)
            finish()
        }
        Latestmess()



    }
    val latestmessmap = HashMap<String,chatMessage>()

    private fun refrecyclermess(){
        adapter.clear()
        latestmessmap.values.forEach{
            adapter.add(LatestMessages(it))
        }
    }
    private fun Latestmess(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-mess/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatmess = snapshot.getValue(chatMessage::class.java)?: return
                latestmessmap[snapshot.key!!] = chatmess
                textView_start_chatting.text=""
                refrecyclermess()
                //adapter.add(LatestMessages(chatmess))

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatmes = snapshot.getValue(chatMessage::class.java)?: return
                textView_start_chatting.text=""
                adapter.add(LatestMessages(chatmes))

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}
val adapter = GroupAdapter<GroupieViewHolder>()
class LatestMessages(val chatMessage: chatMessage): Item<GroupieViewHolder>(){
    var chatpartid : Userdata? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView20.text= chatMessage.text
        //val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
       // val now_time = System.currentTimeMillis()
        //var time_mess=now_time-chatMessage.time
        //val date_time =LocalDateTime.now().toLocalTime()
        viewHolder.itemView.textView21.text= chatMessage.time.toString()
        val chatpartener: String
        if(chatMessage.fromid == FirebaseAuth.getInstance().uid){
            chatpartener=chatMessage.toid
        }
        else
            chatpartener = chatMessage.fromid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatpartener")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatpartid = snapshot.getValue(Userdata::class.java)
                if (chatpartid != null) {
                    viewHolder.itemView.textView19.text= chatpartid!!.username
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun getLayout(): Int {
        return  R.layout.messagesent
    }

}
