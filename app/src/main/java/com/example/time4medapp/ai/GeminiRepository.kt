package com.example.time4medapp.ai

class GeminiRepository {

    // Memory of last few user messages
    private val conversationMemory = mutableListOf<String>()

    suspend fun sendMessage(userMessage: String): String {

        val message = userMessage.lowercase().trim()

        // Store last 5 messages only
        conversationMemory.add(message)
        if (conversationMemory.size > 5) {
            conversationMemory.removeAt(0)
        }

        val mentionedFeverBefore = conversationMemory.any { it.contains("fever") }
        val mentionedHeadacheBefore = conversationMemory.any { it.contains("headache") }

        return when {

            // 👋 Greeting
            message.contains("Hello") || message.contains("hi")  || message.contains("hello") || message.contains("hey") ->
                "Hello 👋 I'm your Time4Med AI assistant. How are you feeling today?"

            // 😊 User mood positive
            message.contains("good") || message.contains("fine") ->
                "That's great to hear 😊 Remember to stay consistent with your medications."

            // 😔 User mood negative
            message.contains("sad") || message.contains("tired") || message.contains("stressed")->
                "I'm sorry you're feeling that way 💙 Make sure you're resting well and taking your medications on time. If things feel overwhelming, consider speaking with a healthcare professional."

            // 🌡 Fever detection
            message.contains("fever") -> {
                if (mentionedFeverBefore && conversationMemory.size > 1) {
                    "Since you mentioned fever again, please monitor your temperature closely. If it stays above 101°F or lasts more than 2 days, consult a doctor immediately."
                } else {
                    "If you have a fever, stay hydrated, rest properly, and monitor your temperature. Seek medical advice if it becomes high or persistent."
                }
            }

            // 🤕 Headache detection
            message.contains("headache") -> {
                if (mentionedHeadacheBefore && conversationMemory.size > 1) {
                    "You’ve mentioned headaches multiple times. If they are frequent or severe, it would be best to consult a doctor."
                } else {
                    "For mild headaches, rest and hydration may help. If pain continues, please consult a healthcare professional."
                }
            }

            // 💊 Reminder help
            message.contains("reminder") || message.contains("medicine") || message.contains("medication") ->
                "To set a medicine reminder, go to the home screen and tap the + button to add your medication schedule."

            // 🙏 Thank you
            message.contains("thank") ->
                "You're welcome 😊 I'm always here to support your health journey."

            // ❓ User asking what AI can do
            message.contains("what can you do") || message.contains("help") ->
                "I can assist you with medication reminders, basic health guidance, and tracking how you're feeling. Just tell me what's going on."

            // 🧠 Context follow-up (if user just says yes/no)
            message == "yes" || message == "no" ->
                "Could you please share a bit more detail so I can guide you better?"

            // Default response
            else ->
                "I’m here to help with medication reminders and basic health advice. If you're experiencing serious symptoms, please consult a medical professional."
        }
    }
}