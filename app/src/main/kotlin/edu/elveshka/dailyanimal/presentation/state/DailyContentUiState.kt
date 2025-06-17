package edu.elveshka.dailyanimal.presentation.state

import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.model.DailyContent

data class DailyContentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val content: DailyContent? = null,
    val contentType: ContentType = ContentType.DOG,
    val showTooltip: Boolean = false
) 