package com.repository.user

import com.domain.entities.UserEventWithRefs
import kotlinx.coroutines.flow.Flow


interface UserRepository {
    fun myEventsObserveAll(): Flow<List<UserEventWithRefs>>

    suspend fun joinEvent(vararg eventId: String)

    suspend fun unjoinEvent(vararg userEventId: String)

    suspend fun isJoinedEventFlow(eventId: String): Flow<Boolean>
}