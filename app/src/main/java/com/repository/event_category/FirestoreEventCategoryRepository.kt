package com.repository.event_category

import com.domain.entities.Entity
import com.domain.entities.EventCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.internal.toImmutableList


class FirestoreEventCategoryRepository(
    private val db: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : EventCategoryRepository {

    private val collection = db.collection(Entity.EVENT_CATEGORY.collection)

    override fun observeAll(): Flow<List<EventCategory>> = callbackFlow {
        val listener = collection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<EventCategory>()?.copy(id = doc.id)
                }.orEmpty()

                trySend(items).isSuccess
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun insert(vararg eventCategory: EventCategory): List<String> {
        val ids = mutableListOf<String>()

        for (dep in eventCategory) {
            val docRef = if (dep.id.isBlank()) collection.document() else collection.document(dep.id)
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                throw IllegalStateException("Event category with id '${docRef.id}' already exists")
            }

            val newDep = dep.copy(id = docRef.id)
            docRef.set(newDep).await()

            ids.plus(docRef.id)
        }

        return ids.toImmutableList()
    }

    override suspend fun update(vararg eventCategory: EventCategory) {
        for (dep in eventCategory) {
            require(dep.id.isNotBlank()) { "Event category id required for update()" }
            collection.document(dep.id).set(dep).await()
        }
    }

    override suspend fun delete(vararg eventCategory: EventCategory) {
        for (dep in eventCategory) {
            require(dep.id.isNotBlank()) { "Event category id required for delete()" }
            collection.document(dep.id).delete().await()
        }
    }
}
