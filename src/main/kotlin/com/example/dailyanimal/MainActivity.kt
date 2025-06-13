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
import com.example.dailyanimal.api.*

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
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
            .padding(end = 56.dp, top = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, 20f)
                lineTo(size.width - 20f, 20f)
                lineTo(size.width - 20f, 0f)
                lineTo(size.width, 20f)
                lineTo(size.width - 20f, 40f)
                lineTo(size.width - 20f, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = backgroundColor,
                style = Fill
            )
        }

        Text(
            text = "Смени собаку на кота",
            color = textColor,
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                                            quote = quoteResponse.content
                                            quoteAuthor = quoteResponse.author
                                            
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
                                textAlign = TextAlign.Center
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