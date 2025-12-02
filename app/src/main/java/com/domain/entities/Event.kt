package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties
import com.util.normalizeText
import java.util.Date


@Keep
@IgnoreExtraProperties
data class EventFirestore(
    val id: String = "",
    val departmentRef: DocumentReference? = null,
    val eventCategoryRef: DocumentReference? = null,
    val title: String = "",
    val titleNormalized: String = "",
    val description: String = "",
    val location: String = "",
    val locationCoordinates: GeoPoint? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val principalImage: String? = null
)

data class Event(
    val id: String,
    val departmentId: String,
    val eventCategoryId: String,
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val principalImage: String?,
)

data class EditableEvent(
    val id: String? = null,
    val departmentId: String? = null,
    val eventCategoryId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val principalImage: String? = null,
)

data class EventWithRefs(
    val event: Event,
    val department: Department?,
    val eventCategory: EventCategory?
)

fun Event.toEditable(): EditableEvent =
    EditableEvent(
        id = id,
        departmentId = departmentId,
        eventCategoryId = eventCategoryId,
        title = title,
        description = description,
        location = location,
        latitude = latitude,
        longitude = longitude,
        startDate = startDate,
        endDate = endDate,
        principalImage = principalImage
    )

fun EventFirestore.toDomain(): Event = Event(
    id = id,
    departmentId = departmentRef?.id.orEmpty(),
    eventCategoryId = eventCategoryRef?.id.orEmpty(),
    title = title,
    description = description,
    location = location,
    latitude = locationCoordinates?.latitude ?: 0.0,
    longitude = locationCoordinates?.longitude ?: 0.0,
    startDate = startDate?.toDate()?.time ?: 0L,
    endDate = endDate?.toDate()?.time ?: 0L,
    principalImage = principalImage
)

fun Event.toFirestore(db: FirebaseFirestore): EventFirestore = EventFirestore(
    id = id,
    departmentRef = departmentId.takeIf { it.isNotBlank() }?.let {
        db.collection(Entity.DEPARTMENT.collection).document(it)
    },
    eventCategoryRef = eventCategoryId.takeIf { it.isNotBlank() }?.let {
        db.collection(Entity.EVENT_CATEGORY.collection).document(it)
    },
    title = title,
    titleNormalized = normalizeText(title),
    description = description,
    location = location,
    locationCoordinates = GeoPoint(latitude, longitude),
    startDate = Timestamp(Date(startDate)),
    endDate = Timestamp(Date(endDate)),
    principalImage = principalImage
)
