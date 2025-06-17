package edu.elveshka.dailyanimal.presentation.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.elveshka.dailyanimal.R
import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import edu.elveshka.dailyanimal.presentation.viewmodel.DailyContentViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
private fun QuoteCard(content: DailyContent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"${content.quote.quoteText}\"",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— ${content.quote.quoteAuthor}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DailyContentScreen(viewModel: DailyContentViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showTooltip by remember { mutableStateOf(false) }
    val tooltipAlpha by animateFloatAsState(
        targetValue = if (showTooltip) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "tooltip_alpha"
    )
    val context = LocalContext.current

    LaunchedEffect(uiState.showTooltip) {
        if (uiState.showTooltip) {
            showTooltip = true
            delay(3000)
            showTooltip = false
            viewModel.hideTooltip()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                    Text(
                        text = if (uiState.contentType == ContentType.DOG) {
                            stringResource(R.string.loading_dog)
                        } else {
                            stringResource(R.string.loading_cat)
                        }
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = stringResource(R.string.error_loading),
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.loadContent() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (uiState.contentType == ContentType.DOG) {
                                stringResource(R.string.new_dog)
                            } else {
                                stringResource(R.string.new_cat)
                            }
                        )
                    }
                }
                uiState.content != null -> {
                    AsyncImage(
                        model = uiState.content?.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "\"${uiState.content?.quote?.quoteText ?: ""}\"",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "— ${uiState.content?.quote?.quoteAuthor?.ifEmpty { stringResource(R.string.unknown_author) } ?: stringResource(R.string.unknown_author)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loadContent() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.contentType == ContentType.DOG) {
                                stringResource(R.string.new_dog)
                            } else {
                                stringResource(R.string.new_cat)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    IconButton(
                        onClick = { takeScreenshot(context) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Camera,
                            contentDescription = stringResource(R.string.take_screenshot),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.showTooltip) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .alpha(tooltipAlpha)
                ) {
                    Text(
                        text = stringResource(R.string.tooltip_switch),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            IconButton(
                onClick = { viewModel.toggleContentType() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = if (uiState.contentType == ContentType.DOG) {
                        stringResource(R.string.switch_to_cats)
                    } else {
                        stringResource(R.string.switch_to_dogs)
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

private fun takeScreenshot(context: Context) {
    try {
        val view = (context as? android.app.Activity)?.window?.decorView?.rootView
        view?.let {
            it.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(it.drawingCache)
            it.isDrawingCacheEnabled = false

            val filename = "DailyAnimal_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { imageUri ->
                context.contentResolver.openOutputStream(imageUri)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
} 