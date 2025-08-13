package com.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.database.dao.*
import com.database.entities.*

@Database(
    entities = [
        Student::class,
        Department::class,
        EventCategory::class,
        Event::class,
        EventStudentCrossRef::class
   ],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun eventCategoryDao(): EventCategoryDao
    abstract fun eventDao(): EventDao
    abstract fun eventStudentDao(): EventStudentDao
}

fun instanceAppDbInMemory(context: Context): AppDb {
    return Room.inMemoryDatabaseBuilder(context, AppDb::class.java)
        .fallbackToDestructiveMigration(false)
        .build()
}
