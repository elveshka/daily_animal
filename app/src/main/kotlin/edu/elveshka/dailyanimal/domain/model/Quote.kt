package edu.elveshka.dailyanimal.domain.model

data class Quote(
    val quoteText: String,
    val quoteAuthor: String? = "Unknown"
)