package com.domain.entities

import androidx.annotation.Keep
import com.domain.Entity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class UserEventFirestore(
    val id: String = "",
    val userId: String = "",
    val eventRef: DocumentReference? = null,
)

data class UserEvent(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
)

data class UserEventWithRefs(
    val id: String,
    val userId: String,
    val event: Event
)

fun UserEventFirestore.toDomain(): UserEvent = UserEvent(
    id = id,
    userId = userId,
    eventId = eventRef?.id.orEmpty()
)

fun UserEvent.toFirestore(db: FirebaseFirestore): UserEventFirestore = UserEventFirestore(
    id = id,
    userId = userId,
    eventRef = eventId.takeIf { it.isNotBlank() }?.let {
        db.collection(Entity.EVENT.collection).document(it)
    }
)
