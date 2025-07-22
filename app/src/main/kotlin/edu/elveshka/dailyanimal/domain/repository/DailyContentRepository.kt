package edu.elveshka.dailyanimal.domain.repository

import edu.elveshka.dailyanimal.domain.enums.AnimalType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import kotlinx.coroutines.flow.Flow

interface DailyContentRepository {
    suspend fun getRandomContent(type: AnimalType): DailyContent
    fun getContentHistory(): Flow<List<DailyContent>>
    suspend fun saveContent(content: DailyContent)
}