package com.example.dailyanimal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.times
import androidx.compose.foundation.layout.width

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DailyAnimalApp()
            }
        }
    }
}

@Composable
fun CustomTooltip() {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
            .padding(end = 56.dp, top = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                // Draw the main rectangle
                moveTo(0f, 20f)
                lineTo(size.width - 20f, 20f)
                // Draw the arrow
                lineTo(size.width - 20f, 0f)
                lineTo(size.width, 20f)
                lineTo(size.width - 20f, 40f)
                // Complete the rectangle
                lineTo(size.width - 20f, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = MaterialTheme.colorScheme.primaryContainer,
                style = Fill
            )
        }

        // Text content
        Text(
            text = "Смени собаку на кота",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun DailyAnimalApp() {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isDogApi by remember { mutableStateOf(true) }
    var quote by remember { mutableStateOf<String?>(null)}
    var quoteAuthor by remember { mutableStateOf<String?>(null)}
    var showTooltip by remember { mutableStateOf(false) }
    var hasShownFirstTooltip by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val alpha by animateFloatAsState(
        targetValue = if (showTooltip) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isDogApi) "Daily Dog" else "Daily Cat",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Box {
                    IconButton(
                        onClick = {
                            isDogApi = !isDogApi
                            imageUrl = null
                            showTooltip = false
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Switch API",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (showTooltip) {
                        Popup(
                            alignment = Alignment.TopEnd,
                            properties = PopupProperties(focusable = false)
                        ) {
                            Box(
                                modifier = Modifier.alpha(alpha)
                            ) {
                                CustomTooltip()
                            }
                        }
                    }
                }
            }

            // Image and Quote Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        error != null -> Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        imageUrl != null -> {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = if (isDogApi) "Dog image" else "Cat image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit,
                                onSuccess = {
                                    scope.launch {
                                        try {
                                            val quoteResponse = QuoteApiClient.service.getQuote()
                                            quote = quoteResponse.quoteText
                                            quoteAuthor = "Elvek Goriaev"
                                            
                                            if (!hasShownFirstTooltip) {
                                                hasShownFirstTooltip = true
                                                showTooltip = true
                                                kotlinx.coroutines.delay(6000)
                                                showTooltip = false
                                            }
                                        } catch (e: Exception) {
                                            quote = "Каждый день - это новая возможность!"
                                            quoteAuthor = "Elvek Goriaev"
                                        }
                                    }
                                }
                            )
                        }
                        else -> Text(
                            if (isDogApi) "Click the button to fetch a dog!"
                            else "Click the button to fetch a cat!",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Quote Card - only show if we have both image and quote
                if (imageUrl != null && quote != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "\"$quote\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "— $quoteAuthor",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        quote = null
                        quoteAuthor = null
                        try {
                            if (isDogApi) {
                                val response = DogApiClient.service.getRandomDog()
                                imageUrl = response.message
                            } else {
                                val response = CatApiClient.service.getRandomCat()
                                imageUrl = response.firstOrNull()?.url
                                    ?: throw Exception("No cat image found")
                            }
                        } catch (e: Exception) {
                            error = e.message
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Fetch"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isLoading) {
                            if (isDogApi) "Woof woof..." else "Meow meow..."
                        } else {
                            if (isDogApi) "Get your DOOOOOOG!" else "Get your CAAAAAT!"
                        }
                    )
                }
            }
        }
    }
}

interface DogApiService {
    @GET("api/breeds/image/random")
    suspend fun getRandomDog(): DogResponse
}

object DogApiClient {
    private const val BASE_URL = "https://dog.ceo/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: DogApiService = retrofit.create(DogApiService::class.java)
}

// Cat API Models and Client
data class CatResponse(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

interface CatApiService {
    @GET("v1/images/search")
    suspend fun getRandomCat(): List<CatResponse>
}

object CatApiClient {
    private const val BASE_URL = "https://api.thecatapi.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: CatApiService = retrofit.create(CatApiService::class.java)
}

// Add new data class for quotes
data class QuoteResponse(
    val quoteText: String,
    val quoteAuthor: String
)

// Add quote service interface
interface QuoteApiService {
    @GET("api/1.0/")
    suspend fun getQuote(
        @Query("method") method: String = "getQuote",
        @Query("format") format: String = "json",
        @Query("lang") lang: String = "ru"
    ): QuoteResponse
}

// Add quote client
object QuoteApiClient {
    private const val BASE_URL = "https://api.forismatic.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: QuoteApiService = retrofit.create(QuoteApiService::class.java)
}