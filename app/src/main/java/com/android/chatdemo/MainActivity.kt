package com.android.chatdemo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.chatdemo.Models.User
import com.android.chatdemo.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mdbref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mdbref = Firebase.database.reference.child("Users")

        userList = ArrayList()
        userAdapter = UserAdapter(this, userList)

        userRecyclerview = binding.recyclerView
        userRecyclerview.adapter = userAdapter
        userRecyclerview.layoutManager = LinearLayoutManager(this)

        mdbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the userList to avoid duplicates
                userList.clear()

                // Iterate through each child node in the snapshot
                for (snap in snapshot.children) {
                    // Attempt to deserialize the data into a User object
                    val user = snap.getValue(User::class.java)

                    // Check if user is not null before adding to the list
                    user?.let {
                        userList.add(it)
                    }
                }
                // Notify the adapter that the data set has changed
                userAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Get the error message from DatabaseError
                val errorMessage = error.message

                // Display the error message in a toast
                Toast.makeText(this@MainActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()

                // Log the error message for debugging purposes
                Log.e("FirebaseDatabase", "Database Error: $errorMessage")
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}