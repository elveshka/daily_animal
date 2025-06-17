package edu.elveshka.dailyanimal.domain.usecase

import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import edu.elveshka.dailyanimal.domain.repository.DailyContentRepository

class GetRandomContentUseCase(
    private val repository: DailyContentRepository
) {
    suspend operator fun invoke(type: ContentType): DailyContent {
        return repository.getRandomContent(type)
    }
} 