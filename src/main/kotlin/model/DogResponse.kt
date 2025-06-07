package model

data class DogResponse(
    val message: String,  // URL of the dog image
    val status: String    // "success" or "error"
) 