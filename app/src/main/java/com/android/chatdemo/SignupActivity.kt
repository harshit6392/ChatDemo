package com.android.chatdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.chatdemo.Models.User
import com.android.chatdemo.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private lateinit var mdbref : DatabaseReference
    private lateinit var user: User
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        user = User()
        binding.btnSignup.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (name.isEmpty() or email.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    binding.progressBar.visibility = View.GONE
                    if (it.isSuccessful) {
                        addUserToDatabase(name, email, mAuth.currentUser!!.uid)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mdbref = FirebaseDatabase.getInstance().reference
        mdbref.child("Users").child(uid).setValue(User(name, email, uid))
        }

    fun onLoginClick(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}