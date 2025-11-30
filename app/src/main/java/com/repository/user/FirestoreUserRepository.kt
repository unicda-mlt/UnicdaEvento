package com.repository.user

import com.domain.entities.Entity
import com.domain.entities.EventFirestore
import com.domain.entities.UserEvent
import com.domain.entities.UserEventFirestore
import com.domain.entities.UserEventWithRefs
import com.domain.entities.UserRole
import com.domain.entities.UserRoleEntity
import com.domain.entities.toDomain
import com.domain.entities.toFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class FirestoreUserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    private val userEvents = db.collection(Entity.USER_EVENT.collection)
    private val events = db.collection(Entity.EVENT.collection)
    private val userWithRoles = db.collection(Entity.USER_WITH_ROLE.collection)

    private fun currentUserId(): String =
        auth.currentUser?.uid ?: error("Not signed in")

    private val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /** Observe all Event docs linked by the signed-in user's user_events documents. */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun myEventsObserveAll(fromDate: Long?): Flow<List<UserEventWithRefs>> =
        authStateFlow.flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                callbackFlow {
                    val uid = currentUserId()

                    val query = userEvents
                        .whereEqualTo("userId", uid)
                        .orderBy("eventRef", Query.Direction.ASCENDING)

                    val listener = query.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        val docs = snapshot?.documents.orEmpty()

                        launch(dispatcher) {
                            val items = docs.mapNotNull { doc ->
                                val ueFs =
                                    doc.toObject<UserEventFirestore>() ?: return@mapNotNull null
                                val ref = ueFs.eventRef ?: return@mapNotNull null

                                val evSnap = ref.get().await()
                                val eventFs = evSnap.toObject<EventFirestore>()?.copy(id = evSnap.id)
                                    ?: return@mapNotNull null

                                val event = eventFs.toDomain()

                                UserEventWithRefs(
                                    id = doc.id,
                                    userId = ueFs.userId,
                                    event = event
                                )
                            }.let { list ->
                                if (fromDate != null) {
                                    list.filter { it.event.startDate >= fromDate }
                                } else {
                                    list
                                }
                            }

                            trySend(items).isSuccess
                        }
                    }

                    awaitClose { listener.remove() }
                }.flowOn(dispatcher)
            }
        }

    override suspend fun joinEvent(vararg eventId: String) {
        val uid = currentUserId()

        for (raw in eventId) {
            val id = raw.trim()
            require(id.isNotEmpty()) { "eventId cannot be blank" }

            val userEventDoc = userEvents.document()

            val payload = UserEvent(
                id = userEventDoc.id,
                userId = uid,
                eventId = id
            ).toFirestore(db)

            userEventDoc.set(payload).await()
        }
    }

    override suspend fun unjoinEvent(vararg userEventId: String) {
        val ids = userEventId.map { it.trim() }.filter { it.isNotEmpty() }.distinct()

        if (ids.isEmpty()) {
            return
        }

        for (chunk in ids.chunked(500)) {
            val batch = db.batch()

            for (id in chunk) {
                batch.delete(userEvents.document(id))
            }

            batch.commit().await()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun isJoinedEventFlow(eventId: String): Flow<Boolean> =
        authStateFlow.flatMapLatest { user ->
            if (user == null) {
                flowOf(false)
            } else {
                callbackFlow {
                    val uid = currentUserId()
                    val eventRef = events.document(eventId)

                    val registration = userEvents
                        .whereEqualTo("userId", uid)
                        .whereEqualTo("eventRef", eventRef)
                        .limit(1)
                        .addSnapshotListener { snap, err ->
                            if (err != null) {
                                trySend(false)
                                return@addSnapshotListener
                            }
                            trySend(snap != null && !snap.isEmpty)
                        }

                    awaitClose { registration.remove() }
                }
            }
        }

    override suspend fun getRole(userId: String): UserRole {
        val userWithRoleDoc = userWithRoles
            .document(userId)
            .get()
            .await()

        if (!userWithRoleDoc.exists()) {
            return UserRole.NO_ROLE
        }

        val roleRef = userWithRoleDoc.getDocumentReference("roleRef")

        if (roleRef == null) {
            return UserRole.NO_ROLE
        }

        val userRoleDoc = roleRef.get().await()

        if (!userRoleDoc.exists()) {
            UserRole.NO_ROLE
        }

        val userRoleEntity = userRoleDoc.toObject<UserRoleEntity>()?.copy(id = userRoleDoc.id)
        val userRole = UserRole.fromValue(userRoleEntity?.name)

        return userRole
    }
}