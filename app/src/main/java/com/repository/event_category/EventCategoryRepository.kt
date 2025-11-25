package com.repository.event_category

import com.domain.RepoResult
import com.domain.entities.EventCategory


interface EventCategoryRepository {
    fun observeAll(
        name: String? = null
    ): kotlinx.coroutines.flow.Flow<List<EventCategory>>

    suspend fun insert(vararg categories: EventCategory): RepoResult<Unit>

    suspend fun update(vararg categories: EventCategory): RepoResult<Unit>

    suspend fun delete(vararg categories: EventCategory)
}
