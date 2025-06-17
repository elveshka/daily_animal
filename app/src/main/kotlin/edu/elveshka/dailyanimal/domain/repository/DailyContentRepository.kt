package edu.elveshka.dailyanimal.domain.repository

import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import kotlinx.coroutines.flow.Flow

interface DailyContentRepository {
    suspend fun getRandomContent(type: ContentType): DailyContent
    fun getContentHistory(): Flow<List<DailyContent>>
    suspend fun saveContent(content: DailyContent)
} 