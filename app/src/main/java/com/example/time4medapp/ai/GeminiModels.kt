package com.example.time4medapp.ai

// 🔹 Request Model

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)


// 🔹 Response Model

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ContentResponse?
)

data class ContentResponse(
    val parts: List<PartResponse>?
)

data class PartResponse(
    val text: String?
)