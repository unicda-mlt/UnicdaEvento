package com.repository.department

import com.domain.RepoResult
import com.domain.entities.Entity
import com.domain.entities.Department
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


class FirestoreDepartmentRepository(
    private val db: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DepartmentRepository {

    private val collection = db.collection(Entity.DEPARTMENT.collection)

    override fun observeAll(name: String?): Flow<List<Department>> = callbackFlow {
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
                    doc.toObject<Department>()?.copy(id = doc.id)
                }.orEmpty()

                trySend(items).isSuccess
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun insert(vararg departments: Department): RepoResult<Unit> {
        return try {
            for (department in departments) {
                val nameNormalized = normalizeText(department.name)

                val existingQuery = collection
                    .whereEqualTo("nameNormalized", nameNormalized)
                    .limit(1)
                    .get()
                    .await()

                if (!existingQuery.isEmpty) {
                    throw IllegalStateException(
                        "Department with name '${department.name}' already exists"
                    )
                }

                val docRef = if (department.id.isBlank()) collection.document() else collection.document(department.id)
                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    throw IllegalStateException("Department with id '${docRef.id}' already exists")
                }

                val newDep = department.copy(id = docRef.id)
                docRef.set(newDep).await()
            }

            RepoResult.Success(Unit)
        } catch (e: IllegalStateException) {
            RepoResult.Error(e.message ?: "")
        } catch (e: Exception) {
            RepoResult.Error("Unexpected error: ${e.message}")
        }

    }

    override suspend fun update(vararg departments: Department): RepoResult<Unit> {
        return try {
            for (department in departments) {
                require(department.id.isNotBlank()) { "Department id required for update()" }

                val nameNormalized = normalizeText(department.name)

                val existingQuery = collection
                    .whereEqualTo("nameNormalized", nameNormalized)
                    .limit(1)
                    .get()
                    .await()

                val existingConflict = existingQuery.documents.firstOrNull()?.let { doc ->
                    doc.id != department.id
                } ?: false

                if (existingConflict) {
                    throw IllegalStateException(
                        "Department with name '${department.name}' already exists"
                    )
                }

                collection.document(department.id).set(department).await()
            }

            RepoResult.Success(Unit)
        } catch (e: IllegalStateException) {
            RepoResult.Error(e.message ?: "")
        } catch (e: Exception) {
            RepoResult.Error("Unexpected error: ${e.message}")
        }
    }

    override suspend fun delete(vararg departments: Department) {
        for (dep in departments) {
            require(dep.id.isNotBlank()) { "Department id required for delete()" }
            collection.document(dep.id).delete().await()
        }
    }
}
