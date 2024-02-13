package com.android.chatdemo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.chatdemo.Models.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var messageTextField: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbref: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().uid

        mDbref = Firebase.database.getReference()


        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name


        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        messageTextField = findViewById(R.id.messageTextField)
        sendButton = findViewById(R.id.buttonSend)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        recyclerViewChat.layoutManager = LinearLayoutManager(this)
        recyclerViewChat.adapter = messageAdapter

        //logic for adding the messages to the recycler view
        mDbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (snap in snapshot.children) {
                        val message = snap.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    val errorMessage = error.message
                }
            })

        // adding the message to the database
        sendButton.setOnClickListener {
            val message = messageTextField.text.toString()
            val messageObject = Message(message, senderUid!!)
            mDbref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject)
                .addOnSuccessListener {
                    mDbref.child("chats").child(receiverRoom!!).child("messages").push().setValue(messageObject)
                        .addOnSuccessListener {
                            messageTextField.setText("")
                        }
                }

        }


    }
}