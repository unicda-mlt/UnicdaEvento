package com.database

import com.database.entities.Department
import com.database.entities.Event
import com.database.entities.EventCategory
import com.database.entities.Student

object SeedData {
    private fun now() = System.currentTimeMillis()
    private const val HOUR = 60 * 60 * 1000L

    suspend fun seed(db: AppDb) {
        val departmentDao = db.departmentDao()
        val categoryDao  = db.eventCategoryDao()
        val studentDao  = db.studentDao()
        val eventDao= db.eventDao()

        //// Departments
        val departments = listOf(
            Department(name = "Computer Science"),
            Department(name = "Mathematics",),
            Department(name = "Arts & Culture")
        )
        val departmentIds = departmentDao.insert(*departments.toTypedArray())

        //// Event categories
        val categories = listOf(
            EventCategory(name = "Seminar",),
            EventCategory(name = "Workshop",),
            EventCategory(name = "Sports",)
        )
        val categoryIds = categoryDao.insert(*categories.toTypedArray())

        //// Students
        val students = listOf(
            Student(name = "Ana", lastName = "García", registrationId = "2025-0001", password = "demo123"),
            Student(name = "Luis", lastName = "Martínez", registrationId = "2025-0002", password = "demo123"),
            Student(name = "Luis", lastName = "Martínez", registrationId = "1", password = "1")
        )
        studentDao.insert(*students.toTypedArray())

        val base = now() + 2 * HOUR
        val events = listOf(
            Event(
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[0],
                title = "Intro to Kotlin",
                description = "Basics of Kotlin and Compose for Android.",
                latitude = 18.4861,
                longitude = -69.9312,
                startDate = base,
                endDate = base + 2 * HOUR,
                principalImage = null
            ),
            Event(
                departmentId = departmentIds[1],
                eventCategoryId = departmentIds[1],
                title = "Math Modeling Workshop",
                description = "Hands-on optimization and simulation.",
                latitude = 18.4861,
                longitude = -69.9312,
                startDate = base + 24 * HOUR,
                endDate = base + 26 * HOUR,
                principalImage = null
            ),
            Event(
                departmentId = departmentIds[2],
                eventCategoryId = departmentIds[2],
                title = "Campus 5K",
                description = "Friendly race around campus.",
                latitude = 18.4861,
                longitude = -69.9312,
                startDate = base + 48 * HOUR,
                endDate = base + 49 * HOUR,
                principalImage = null
            )
        )
        eventDao.insert(*events.toTypedArray())
    }
}
