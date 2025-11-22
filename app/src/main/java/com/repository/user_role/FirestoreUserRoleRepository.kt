package com.repository.user_role


import com.domain.entities.Entity
import com.domain.entities.UserRoleEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await


class FirestoreUserRoleRepository(
    private val db: FirebaseFirestore
) : UserRoleRepository {

    private val collection = db.collection(Entity.USER_ROLE.collection)

    override suspend fun getAll(): List<UserRoleEntity> {
        val snapshot = collection
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot?.documents?.mapNotNull { doc ->
            doc.toObject<UserRoleEntity>()?.copy(id = doc.id)
        }.orEmpty()
    }

    override suspend fun getByName(name: String): UserRoleEntity? {
        val result = collection
            .whereEqualTo("name", name)
            .limit(1)
            .get()
            .await()

        val doc = result.documents.firstOrNull()

        return doc?.toObject<UserRoleEntity>()?.copy(id = doc.id)
    }
}
