package com.repository.department

import com.domain.Entity
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
import kotlinx.coroutines.CoroutineDispatcher


class FirestoreDepartmentRepository(
    private val db: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DepartmentRepository {

    private val collection = db.collection(Entity.DEPARTMENT.collection)

    override fun observeAll(): Flow<List<Department>> = callbackFlow {
        val listener = collection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
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

    override suspend fun insert(vararg department: Department) {
        for (dep in department) {
            val docRef = if (dep.id.isBlank()) collection.document() else collection.document(dep.id)
            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                throw IllegalStateException("Department with id '${docRef.id}' already exists")
            }

            val newDep = dep.copy(id = docRef.id)
            docRef.set(newDep).await()
        }
    }

    override suspend fun update(vararg department: Department) {
        for (dep in department) {
            require(dep.id.isNotBlank()) { "Department id required for update()" }
            collection.document(dep.id).set(dep).await()
        }
    }

    override suspend fun delete(vararg department: Department) {
        for (dep in department) {
            require(dep.id.isNotBlank()) { "Department id required for delete()" }
            collection.document(dep.id).delete().await()
        }
    }
}
