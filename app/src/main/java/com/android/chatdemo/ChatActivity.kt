package com.android.chatdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.chatdemo.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    //binding
    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}