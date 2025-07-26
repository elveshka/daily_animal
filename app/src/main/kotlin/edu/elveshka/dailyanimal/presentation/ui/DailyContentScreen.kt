package edu.elveshka.dailyanimal.presentation.ui

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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.elveshka.dailyanimal.R
import edu.elveshka.dailyanimal.domain.enums.AnimalType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import edu.elveshka.dailyanimal.presentation.viewmodel.DailyContentViewModel
import kotlinx.coroutines.delay

@Composable
private fun QuoteCard(content: DailyContent) {
    val author = content.quote.quoteAuthor!!.ifEmpty { stringResource(R.string.unknown_author) }
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
                text = "— $author",
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
                        text = if (uiState.contentType == AnimalType.DOG) {
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
                            text = if (uiState.contentType == AnimalType.DOG) {
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

                    QuoteCard(content = uiState.content!!)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loadContent() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.contentType == AnimalType.DOG) {
                                stringResource(R.string.new_dog)
                            } else {
                                stringResource(R.string.new_cat)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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
                    contentDescription = if (uiState.contentType == AnimalType.DOG) {
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
