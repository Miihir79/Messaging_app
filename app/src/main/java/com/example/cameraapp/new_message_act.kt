package com.example.cameraapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_display.view.*

class new_message_act : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        imageButton_back.setOnClickListener {
            val intent = Intent(this,Message::class.java)
            startActivity(intent)
        }



        fetchuser()

    }
    companion object{
        val USER_KEY="USER_KEY"
    }

    private fun fetchuser(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{

                    val user = it.getValue(Userdata::class.java)
                    if(user != null){
                        adapter.add(Useritem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val useritem = item as Useritem
                    val intent = Intent(view.context,Chatlog::class.java)
                    intent.putExtra(USER_KEY,useritem.user)
                    startActivity(intent)
                    finish()
                }
                recycler.adapter = adapter
            }
        })

    }
}
class Useritem(val user:Userdata): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView15.text=user.username

    }

    override fun getLayout(): Int {
        return R.layout.user_row_display


    }

}