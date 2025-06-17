package edu.elveshka.dailyanimal.data.repository

import edu.elveshka.dailyanimal.data.api.AnimalApi
import edu.elveshka.dailyanimal.data.api.QuoteApi
import edu.elveshka.dailyanimal.domain.model.ContentType
import edu.elveshka.dailyanimal.domain.model.DailyContent
import edu.elveshka.dailyanimal.domain.repository.DailyContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DailyContentRepositoryImpl(
    private val animalApi: AnimalApi,
    private val quoteApi: QuoteApi
) : DailyContentRepository {

    override suspend fun getRandomContent(type: ContentType): DailyContent = withContext(Dispatchers.IO) {
        val quote = quoteApi.getRandomQuote()
        val imageUrl = when (type) {
            ContentType.DOG -> animalApi.getRandomDog().message
            ContentType.CAT -> animalApi.getRandomCat().first().url
        }
        DailyContent(
            imageUrl = imageUrl,
            quote = quote,
            type = type
        )
    }

    override fun getContentHistory(): Flow<List<DailyContent>> = flow {
        // TODO: Implement local database storage
        emit(emptyList())
    }

    override suspend fun saveContent(content: DailyContent) {
        // TODO: Implement local database storage
    }
}