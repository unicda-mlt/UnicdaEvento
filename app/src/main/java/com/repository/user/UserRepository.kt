package com.repository.user

import com.domain.entities.UserEventWithRefs
import com.domain.entities.UserRole
import kotlinx.coroutines.flow.Flow


interface UserRepository {
    fun myEventsObserveAll(
        fromDate: Long? = null
    ): Flow<List<UserEventWithRefs>>

    suspend fun joinEvent(vararg eventId: String)

    suspend fun unjoinEvent(vararg userEventId: String)

    suspend fun isJoinedEventFlow(eventId: String): Flow<Boolean>

    suspend fun getRole(userId: String): UserRole
}