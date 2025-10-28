package com.repository.event_category

import com.domain.entities.EventCategory


interface EventCategoryRepository {
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<EventCategory>>

    suspend fun insert(vararg eventCategory: EventCategory): List<String>

    suspend fun update(vararg eventCategory: EventCategory)

    suspend fun delete(vararg eventCategory: EventCategory)
}
