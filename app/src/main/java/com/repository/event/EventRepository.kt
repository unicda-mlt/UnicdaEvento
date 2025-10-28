package com.repository.event

import com.domain.entities.Event
import com.domain.entities.EventWithRefs
import kotlinx.coroutines.flow.Flow


interface EventRepository {
    suspend fun getAll(
        titlePrefix: String? = null,
        fromDate: Long? = null,
        toDate: Long? = null,
        departmentId: String? = null,
        categoryEventId: String? = null
    ): List<Event>

    fun observeWithRefs(id: String): Flow<EventWithRefs?>

    suspend fun insert(vararg e: Event): List<String>
    suspend fun update(vararg e: Event)
    suspend fun delete(vararg e: Event)
}
