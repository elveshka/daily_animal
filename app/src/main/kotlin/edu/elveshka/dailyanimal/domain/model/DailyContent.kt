package edu.elveshka.dailyanimal.domain.model

import edu.elveshka.dailyanimal.data.api.QuoteResponse

data class DailyContent(
    val imageUrl: String,
    val quote: QuoteResponse,
    val type: ContentType
)

enum class ContentType {
    DOG,
    CAT
} 