package com.database

import com.database.entities.Department
import com.database.entities.Event
import com.database.entities.EventCategory
import com.database.entities.EventStudentCrossRef
import com.database.entities.Student


object SeedData {
    private fun now() = System.currentTimeMillis()
    private const val HOUR = 60 * 60 * 1000L
    private const val DAY = 24 * HOUR
    private val BASE_TIME = now() + 2 * HOUR

    private const val SD_LAT = 18.4861 // Santo Domingo Latitude
    private const val SD_LNG = -69.9312 // Santo Domingo Longitude
    private const val STI_LAT = 19.4517 // Santiago Latitude
    private const val STI_LNG = -70.6970 // Santo Domingo Longitude


    suspend fun seed(db: AppDb) {
        val departmentDao = db.departmentDao()
        val categoryDao  = db.eventCategoryDao()
        val studentDao  = db.studentDao()
        val eventDao= db.eventDao()
        val eventStudentDao = db.eventStudentDao()

        //// Departments
        val departments = listOf(
            Department(name = "Computer Science"),
            Department(name = "Mathematics"),
            Department(name = "Arts & Culture"),
            Department(name = "Business & Entrepreneurship"),
            Department(name = "Electrical Engineering"),
            Department(name = "Health Sciences"),
            Department(name = "Architecture & Design"),
            Department(name = "Social Sciences")
        )
        val departmentIds = departmentDao.insert(*departments.toTypedArray())

        //// Event categories
        val categories = listOf(
            EventCategory(name = "Seminar"),
            EventCategory(name = "Workshop"),
            EventCategory(name = "Sports"),
            EventCategory(name = "Hackathon"),
            EventCategory(name = "Lecture"),
            EventCategory(name = "Career Fair"),
            EventCategory(name = "Conference"),
            EventCategory(name = "Meetup")
        )
        val categoryIds = categoryDao.insert(*categories.toTypedArray())

        //// Students
        val students = listOf(
            Student(id = 1L, name = "Luis", lastName = "Martínez", registrationId = "1", password = "1"),
            Student(id = 2L, name = "Ana", lastName = "García", registrationId = "2025-0001", password = "demo123"),
            Student(id = 3L, name = "Luis", lastName = "Martínez", registrationId = "2025-0002", password = "demo123"),
        )
        studentDao.insert(*students.toTypedArray())

        //// Events
        val events = listOf(
            // 0
            Event(
                id = 1L,
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[0],
                title = "Intro to Kotlin",
                description = "Basics of Kotlin and Compose for Android. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 2 * HOUR, endDate = BASE_TIME + 4 * HOUR,
                principalImage = "https://picsum.photos/seed/1/1280/720"
            ),
            // 1
            Event(
                id = 2L,
                departmentId = departmentIds[1],
                eventCategoryId = categoryIds[1],
                title = "Math Modeling Workshop",
                description = "Hands-on optimization and simulation. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 1 * DAY, endDate = BASE_TIME + 1 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/2/1280/720"
            ),
            // 2
            Event(
                id = 3L,
                departmentId = departmentIds[2],
                eventCategoryId = categoryIds[2],
                title = "Campus 5K",
                description = "Friendly race around campus. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 2 * DAY, endDate = BASE_TIME + 2 * DAY + 1 * HOUR,
                principalImage = "https://picsum.photos/seed/3/1280/720"
            ),

            // 3
            Event(
                id = 4L,
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[3],
                title = "UNICDA 12-Hour Hackathon",
                description = "Build solutions for student life. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 3 * DAY, endDate = BASE_TIME + 3 * DAY + 12 * HOUR,
                principalImage = "https://picsum.photos/seed/4/1280/720"
            ),
            // 4
            Event(
                id = 5L,
                departmentId = departmentIds[3],
                eventCategoryId = categoryIds[4],
                title = "Startup Finance 101",
                description = "Unit economics and funding basics. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 3 * DAY + 4 * HOUR, endDate = BASE_TIME + 3 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/5/1280/720"
            ),
            // 5
            Event(
                id = 6L,
                departmentId = departmentIds[5],
                eventCategoryId = categoryIds[0],
                title = "Public Health in Urban Settings",
                description = "Community health strategies in DR. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 4 * DAY, endDate = BASE_TIME + 4 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/6/1280/720"
            ),
            // 6
            Event(
                id = 7L,
                departmentId = departmentIds[4],
                eventCategoryId = categoryIds[1],
                title = "Intro to Microcontrollers",
                description = "Hands-on Arduino fundamentals. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 4 * DAY + 3 * HOUR, endDate = BASE_TIME + 4 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/7/1280/720"
            ),
            // 7
            Event(
                id = 8L,
                departmentId = departmentIds[6],
                eventCategoryId = categoryIds[4],
                title = "Sustainable Architecture in the Tropics",
                description = "Materials and passive cooling. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 5 * DAY, endDate = BASE_TIME + 5 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/8/1280/720"
            ),
            // 8
            Event(
                id = 9L,
                departmentId = departmentIds[7],
                eventCategoryId = categoryIds[7],
                title = "Civic Tech Meetup",
                description = "Tech for public services in DR. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 5 * DAY + 5 * HOUR, endDate = BASE_TIME + 5 * DAY + 8 * HOUR,
                principalImage = "https://picsum.photos/seed/9/1280/720"
            ),
            // 9
            Event(
                id = 10L,
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[5],
                title = "Tech Career Fair",
                description = "Local companies hiring interns & grads. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 6 * DAY, endDate = BASE_TIME + 6 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/10/1280/720"
            ),
            // 10
            Event(
                id = 11L,
                departmentId = departmentIds[3],
                eventCategoryId = categoryIds[6],
                title = "Entrepreneurship Summit UNICDA",
                description = "Talks, panels, and networking. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 7 * DAY, endDate = BASE_TIME + 7 * DAY + 8 * HOUR,
                principalImage = "https://picsum.photos/seed/11/1280/720"
            ),
            // 11
            Event(
                id = 12L,
                departmentId = departmentIds[2],
                eventCategoryId = categoryIds[7],
                title = "Open Mic Night",
                description = "Poetry, music, and stand-up. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 7 * DAY + 3 * HOUR, endDate = BASE_TIME + 7 * DAY + 5 * HOUR,
                principalImage = "https://picsum.photos/seed/12/1280/720"
            ),
            // 12
            Event(
                id = 13L,
                departmentId = departmentIds[1],
                eventCategoryId = categoryIds[0],
                title = "Probability for Data Science",
                description = "Distributions, Bayes, and intuition. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 8 * DAY, endDate = BASE_TIME + 8 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/13/1280/720"
            ),
            // 13
            Event(
                id = 14L,
                departmentId = departmentIds[4],
                eventCategoryId = categoryIds[4],
                title = "Power Systems in the Caribbean",
                description = "Grid challenges and innovation. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 8 * DAY + 4 * HOUR, endDate = BASE_TIME + 8 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/14/1280/720"
            ),
            // 14
            Event(
                id = 15L,
                departmentId = departmentIds[6],
                eventCategoryId = categoryIds[1],
                title = "UX for Mobile Apps",
                description = "Wireframes to micro-interactions. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 9 * DAY, endDate = BASE_TIME + 9 * DAY + 3 * HOUR,
                principalImage = "https://picsum.photos/seed/15/1280/720"
            ),
            // 15
            Event(
                id = 16L,
                departmentId = departmentIds[5],
                eventCategoryId = categoryIds[2],
                title = "Interfaculty Basketball",
                description = "Friendly tournament. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 9 * DAY + 6 * HOUR, endDate = BASE_TIME + 9 * DAY + 10 * HOUR,
                principalImage = "https://picsum.photos/seed/16/1280/720"
            ),
            // 16
            Event(
                id = 17L,
                departmentId = departmentIds[7],
                eventCategoryId = categoryIds[0],
                title = "Media Literacy & Misinformation",
                description = "Tools to evaluate news. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 10 * DAY, endDate = BASE_TIME + 10 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/17/1280/720"
            ),
            // 17
            Event(
                id = 18L,
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[1],
                title = "Compose Multiplatform Basics",
                description = "Build once for desktop & mobile. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 10 * DAY + 4 * HOUR, endDate = BASE_TIME + 10 * DAY + 7 * HOUR,
                principalImage = "https://picsum.photos/seed/18/1280/720"
            ),
            // 18
            Event(
                id = 19L,
                departmentId = departmentIds[3],
                eventCategoryId = categoryIds[7],
                title = "Founders & Coffee",
                description = "Share early-stage stories. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 11 * DAY, endDate = BASE_TIME + 11 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/19/1280/720"
            ),
            // 19
            Event(
                id = 20L,
                departmentId = departmentIds[1],
                eventCategoryId = categoryIds[4],
                title = "Math of Cryptography",
                description = "From primes to protocols. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 11 * DAY + 3 * HOUR, endDate = BASE_TIME + 11 * DAY + 5 * HOUR,
                principalImage = "https://picsum.photos/seed/20/1280/720"
            ),
            // 20
            Event(
                id = 21L,
                departmentId = departmentIds[4],
                eventCategoryId = categoryIds[6],
                title = "IoT & Smart Cities Day",
                description = "Panels, demos, and posters. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 12 * DAY, endDate = BASE_TIME + 12 * DAY + 8 * HOUR,
                principalImage = "https://picsum.photos/seed/21/1280/720"
            ),
            // 21
            Event(
                id = 22L,
                departmentId = departmentIds[2],
                eventCategoryId = categoryIds[5],
                title = "Creative Careers Expo",
                description = "Design, media, and arts roles. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 12 * DAY + 2 * HOUR, endDate = BASE_TIME + 12 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/22/1280/720"
            ),
            // 22
            Event(
                id = 23L,
                departmentId = departmentIds[6],
                eventCategoryId = categoryIds[0],
                title = "Design Systems for Scale",
                description = "Tokens, theming, and docs. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 13 * DAY, endDate = BASE_TIME + 13 * DAY + 2 * HOUR,
                principalImage = "https://picsum.photos/seed/23/1280/720"
            ),
            // 23
            Event(
                id = 24L,
                departmentId = departmentIds[5],
                eventCategoryId = categoryIds[1],
                title = "First Aid Basics",
                description = "CPR, bleeding control, and more. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 13 * DAY + 3 * HOUR, endDate = BASE_TIME + 13 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/24/1280/720"
            ),
            // 24
            Event(
                id = 25L,
                departmentId = departmentIds[7],
                eventCategoryId = categoryIds[6],
                title = "Policy & Technology Forum",
                description = "Privacy, AI governance, and society. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 14 * DAY, endDate = BASE_TIME + 14 * DAY + 7 * HOUR,
                principalImage = "https://picsum.photos/seed/25/1280/720"
            ),
            // 25
            Event(
                id = 26L,
                departmentId = departmentIds[0],
                eventCategoryId = categoryIds[4],
                title = "Modern JVM Performance",
                description = "GC tuning & profiling for Kotlin. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 14 * DAY + 4 * HOUR, endDate = BASE_TIME + 14 * DAY + 6 * HOUR,
                principalImage = "https://picsum.photos/seed/26/1280/720"
            ),
            // 26
            Event(
                id = 27L,
                departmentId = departmentIds[3],
                eventCategoryId = categoryIds[1],
                title = "Pitch Deck Clinic",
                description = "Story, traction, and metrics. Hosted by UNICDA.",
                location = "Santo Domingo",
                latitude = SD_LAT, longitude = SD_LNG,
                startDate = BASE_TIME + 15 * DAY, endDate = BASE_TIME + 15 * DAY + 3 * HOUR,
                principalImage = "https://picsum.photos/seed/27/1280/720"
            ),
            // 27
            Event(
                id = 28L,
                departmentId = departmentIds[4],
                eventCategoryId = categoryIds[2],
                title = "Robotics Soccer Friendly",
                description = "Student-built bots on the field. Hosted by UNICDA.",
                location = "Santiago",
                latitude = STI_LAT, longitude = STI_LNG,
                startDate = BASE_TIME + 15 * DAY + 6 * HOUR, endDate = BASE_TIME + 15 * DAY + 9 * HOUR,
                principalImage = "https://picsum.photos/seed/28/1280/720"
            )
        )
        eventDao.insert(*events.toTypedArray())

        //// Event - Student
        val studentId1 = students[0]
        val pick = listOf(1L, 3L, 6L, 9L, 14L, 20L)

        val refs = pick.map { idx ->
            EventStudentCrossRef(eventId = idx, studentId = studentId1.id)
        }
        eventStudentDao.upsertAll(refs)
    }
}
