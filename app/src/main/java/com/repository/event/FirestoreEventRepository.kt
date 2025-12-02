package com.repository.event

import com.di.CoroutineDispatchersModule
import com.domain.RepoResult
import com.domain.entities.Entity
import com.domain.entities.Department
import java.util.Date
import com.domain.entities.Event
import com.domain.entities.EventCategory
import com.domain.entities.EventFirestore
import com.domain.entities.EventWithRefs
import com.domain.entities.toDomain
import com.domain.entities.toFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.util.FirestoreUtils
import com.util.normalizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreEventRepository @Inject constructor(
    private val db: FirebaseFirestore,
    @CoroutineDispatchersModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) : EventRepository {

    private val collection = db.collection(Entity.EVENT.collection)

    override suspend fun getAll(
        titlePrefix: String?,
        fromDate: Long?,
        toDate: Long?,
        departmentId: String?,
        categoryEventId: String?
    ): List<Event> = withContext(dispatcher) {
        var q: Query = collection

        if (!departmentId.isNullOrBlank()) {
            q = q.whereEqualTo(
                "departmentRef",
                db.collection(Entity.DEPARTMENT.collection).document(departmentId)
            )
        }

        if (!categoryEventId.isNullOrBlank()) {
            q = q.whereEqualTo(
                "eventCategoryRef",
                db.collection(Entity.EVENT_CATEGORY.collection).document(categoryEventId)
            )
        }

        if (fromDate != null) {
            q = q.whereGreaterThanOrEqualTo("startDate", Timestamp(Date(fromDate)))
        }

        if (toDate != null) {
            q = q.whereLessThanOrEqualTo("startDate", Timestamp(Date(toDate)))
        }

        if (!titlePrefix.isNullOrBlank()) {
            q = q
                .orderBy("titleNormalized", Query.Direction.ASCENDING)
                .orderBy("startDate", Query.Direction.ASCENDING)

            val titlePrefixNormalized = normalizeText(titlePrefix)
            val startBound = Timestamp(Date(fromDate ?: 0L))
            val endBound =
                toDate?.let { Timestamp(Date(it)) } ?: FirestoreUtils.MAX_FIRESTORE_TIMESTAMP

            q = q.startAt(titlePrefixNormalized, startBound)
                .endAt(titlePrefixNormalized + "\uf8ff", endBound)
        } else {
            q = q.orderBy("startDate", Query.Direction.ASCENDING)
        }

        val snapshot = q.get().await()
        snapshot.documents.mapNotNull { d ->
            d.toObject(EventFirestore::class.java)?.copy(id = d.id)?.toDomain()
        }.sortedBy { it.startDate }
    }

    override fun observeWithRefs(id: String): Flow<EventWithRefs?> = callbackFlow {
        require(id.isNotBlank()) { "id required" }
        val docRef = collection.document(id)

        val reg = docRef.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            if (snap == null || !snap.exists()) {
                trySend(null).isSuccess
                return@addSnapshotListener
            }

            launch(CoroutineScope(dispatcher).coroutineContext) {
                val ef = snap.toObject(EventFirestore::class.java)?.copy(id = snap.id)
                if (ef == null) {
                    trySend(null).isSuccess
                    return@launch
                }

                val dep = ef.departmentRef?.get()?.await()
                    ?.toObject(Department::class.java)
                    ?.copy(id = ef.departmentRef.id)

                val cat = ef.eventCategoryRef?.get()?.await()
                    ?.toObject(EventCategory::class.java)
                    ?.copy(id = ef.eventCategoryRef.id)

                trySend(
                    EventWithRefs(
                        event = ef.toDomain(),
                        department = dep,
                        eventCategory = cat
                    )
                ).isSuccess
            }
        }

        awaitClose { reg.remove() }
    }.flowOn(dispatcher)

    override suspend fun insert(vararg events: Event): RepoResult<List<String>> =
        withContext(dispatcher) {
            try {
                val ids = mutableListOf<String>()

                for (event in events) {
                    val docRef = if (event.id.isBlank()) {
                        collection.document()
                    } else {
                        collection.document(event.id)
                    }

                    val snapshot = docRef.get().await()
                    if (snapshot.exists()) {
                        throw IllegalStateException("Event with id '${docRef.id}' already exists")
                    }

                    val toSave = event.copy(id = docRef.id).toFirestore(db)
                    docRef.set(toSave).await()
                    ids += docRef.id
                }

                RepoResult.Success(ids)
            } catch (e: IllegalStateException) {
                RepoResult.Error(e.message ?: "")
            } catch (e: Exception) {
                RepoResult.Error("Unexpected error: ${e.message}")
            }
        }

    override suspend fun update(vararg events: Event): RepoResult<Unit> =
        withContext(dispatcher) {
            try {
                for (event in events) {
                    require(event.id.isNotBlank()) { "id required for update()" }

                    val docRef = collection.document(event.id)
                    val snapshot = docRef.get().await()

                    if (!snapshot.exists()) {
                        throw IllegalStateException("Event with id '${event.id}' does not exist")
                    }

                    val toSave = event.toFirestore(db)
                    docRef.set(toSave).await()
                }

                RepoResult.Success(Unit)

            } catch (e: IllegalStateException) {
                RepoResult.Error(e.message ?: "")
            } catch (e: Exception) {
                RepoResult.Error("Unexpected error: ${e.message}")
            }
        }

    override suspend fun delete(vararg e: Event) = withContext(dispatcher) {
        for (item in e) {
            require(item.id.isNotBlank()) { "id required for delete()" }
            collection.document(item.id).delete().await()
        }
    }

    override suspend fun getById(id: String): Event? = withContext(dispatcher) {
        require(id.isNotBlank()) { "id required" }
        val docRef = collection.document(id)

        val snap = docRef.get().await()
        if (!snap.exists()) return@withContext null

        val event = snap.toObject(EventFirestore::class.java)?.copy(id = snap.id)
            ?: return@withContext null

        event.toDomain()
    }
}
