package com.repository.event_category

import com.domain.RepoResult
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
import com.util.normalizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class FirestoreEventCategoryRepository(
    private val db: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : EventCategoryRepository {

    private val collection = db.collection(Entity.EVENT_CATEGORY.collection)

    override fun observeAll(name: String?): Flow<List<EventCategory>> = callbackFlow {
        val query = if (name.isNullOrBlank()) {
            collection.orderBy("nameNormalized", Query.Direction.ASCENDING)
        } else {
            val nameNormalized = normalizeText(name)

            collection
                .orderBy("nameNormalized", Query.Direction.ASCENDING)
                .startAt(nameNormalized)
                .endAt(nameNormalized + "\uf8ff")
        }

        val listener = query.addSnapshotListener { snapshot, error ->
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

    override suspend fun getAll(name: String?): List<EventCategory> = withContext(dispatcher) {
        val query = if (name.isNullOrBlank()) {
            collection.orderBy("nameNormalized", Query.Direction.ASCENDING)
        } else {
            val nameNormalized = normalizeText(name)

            collection
                .orderBy("nameNormalized", Query.Direction.ASCENDING)
                .startAt(nameNormalized)
                .endAt(nameNormalized + "\uf8ff")
        }

        val snapshot = query.get().await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject<EventCategory>()?.copy(id = doc.id)
        }
    }

    override suspend fun insert(vararg categories: EventCategory): RepoResult<Unit> {
        return try {
            for (category in categories) {
                val nameNormalized = normalizeText(category.name)

                val existingQuery = collection
                    .whereEqualTo("nameNormalized", nameNormalized)
                    .limit(1)
                    .get()
                    .await()

                if (!existingQuery.isEmpty) {
                    throw IllegalStateException(
                        "Category with name '${category.name}' already exists"
                    )
                }

                val docRef = if (category.id.isBlank()) collection.document() else collection.document(category.id)
                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    throw IllegalStateException("Category with id '${docRef.id}' already exists")
                }

                val newDep = category.copy(id = docRef.id)
                docRef.set(newDep).await()
            }

            RepoResult.Success(Unit)
        } catch (e: IllegalStateException) {
            RepoResult.Error(e.message ?: "")
        } catch (e: Exception) {
            RepoResult.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun update(vararg categories: EventCategory): RepoResult<Unit> {
        return try {
            for (category in categories) {
                require(category.id.isNotBlank()) { "Category id required for update()" }

                val nameNormalized = normalizeText(category.name)

                val existingQuery = collection
                    .whereEqualTo("nameNormalized", nameNormalized)
                    .limit(1)
                    .get()
                    .await()

                val existingConflict = existingQuery.documents.firstOrNull()?.let { doc ->
                    doc.id != category.id
                } ?: false

                if (existingConflict) {
                    throw IllegalStateException(
                        "Category with name '${category.name}' already exists"
                    )
                }

                collection.document(category.id).set(category).await()
            }

            RepoResult.Success(Unit)
        } catch (e: IllegalStateException) {
            RepoResult.Error(e.message ?: "")
        } catch (e: Exception) {
            RepoResult.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun delete(vararg categories: EventCategory) {
        for (category in categories) {
            require(category.id.isNotBlank()) { "Event category id required for delete()" }
            collection.document(category.id).delete().await()
        }
    }
}
