package com.example.time4medapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.time4medapp.ai.GeminiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AIChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonBack: ImageView

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    private val geminiRepository = GeminiRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide default ActionBar
        supportActionBar?.hide()

        setContentView(R.layout.activity_ai_chat)

        recyclerView = findViewById(R.id.recycler_view_chat)
        editTextMessage = findViewById(R.id.edit_text_message)
        buttonSend = findViewById(R.id.button_send)
        buttonBack = findViewById(R.id.button_back)

        chatAdapter = ChatAdapter(messageList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Back button click
        buttonBack.setOnClickListener {
            finish()
        }

        buttonSend.setOnClickListener {

            val userMessage = editTextMessage.text.toString().trim()

            if (userMessage.isNotEmpty()) {

                // Add user message
                chatAdapter.addMessage(ChatMessage(userMessage, true))
                recyclerView.scrollToPosition(messageList.size - 1)
                editTextMessage.text.clear()

                lifecycleScope.launch {

                    // Add typing indicator
                    val typingMessage = ChatMessage("Typing...", false)
                    chatAdapter.addMessage(typingMessage)
                    recyclerView.scrollToPosition(messageList.size - 1)

                    // Simulate thinking delay
                    delay(2000)

                    // Remove typing indicator
                    messageList.removeAt(messageList.size - 1)
                    chatAdapter.notifyItemRemoved(messageList.size)

                    // Get AI response
                    val aiResponse = geminiRepository.sendMessage(userMessage)

                    // Add real AI response
                    chatAdapter.addMessage(ChatMessage(aiResponse, false))
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }
        }
    }
}