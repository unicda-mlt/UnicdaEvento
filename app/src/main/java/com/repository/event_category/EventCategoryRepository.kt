package com.repository.event_category

import com.domain.RepoResult
import com.domain.entities.EventCategory
import kotlinx.coroutines.flow.Flow


interface EventCategoryRepository {
    fun observeAll(
        name: String? = null
    ): Flow<List<EventCategory>>

    suspend fun getAll(
        name: String? = null
    ): List<EventCategory>

    suspend fun insert(vararg categories: EventCategory): RepoResult<Unit>

    suspend fun update(vararg categories: EventCategory): RepoResult<Unit>

    suspend fun delete(vararg categories: EventCategory)
}
