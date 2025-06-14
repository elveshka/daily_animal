package edu.elveshka.dailyanimal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import edu.elveshka.dailyanimal.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DailyAnimalApp(viewModel)
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
fun DailyAnimalApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val alpha by animateFloatAsState(
        targetValue = if (uiState.showTooltip) 1f else 0f,
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
                    if (uiState.isDogApi) "Daily Dog" else "Daily Cat",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Box {
                    IconButton(
                        onClick = { viewModel.toggleApi() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Switch API",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (uiState.showTooltip) {
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
                        uiState.isLoading -> CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )

                        uiState.error != null -> Text(
                            uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )

                        uiState.imageUrl != null -> {
                            AsyncImage(
                                model = uiState.imageUrl,
                                contentDescription = if (uiState.isDogApi) "Dog image" else "Cat image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        else -> Text(
                            if (uiState.isDogApi) "Click the button to fetch a dog!"
                            else "Click the button to fetch a cat!",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (uiState.imageUrl != null && uiState.quote != null) {
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
                                text = "\"${uiState.quote}\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "— ${uiState.quoteAuthor}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.loadImage() },
                enabled = !uiState.isLoading,
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
                        if (uiState.isLoading) {
                            if (uiState.isDogApi) "Woof woof..." else "Meow meow..."
                        } else {
                            if (uiState.isDogApi) "Get your DOOOOOOG!" else "Get your CAAAAAT!"
                        }
                    )
                }
            }
        }
    }
}