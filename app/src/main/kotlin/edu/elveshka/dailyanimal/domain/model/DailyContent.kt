package edu.elveshka.dailyanimal.domain.model

import edu.elveshka.dailyanimal.domain.enums.AnimalType

data class DailyContent(
    val id: String,
    val type: AnimalType,
    val imageUrl: String,
    val quote: Quote,
)
