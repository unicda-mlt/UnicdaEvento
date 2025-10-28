package com.repository.department

import com.domain.entities.Department


interface DepartmentRepository {
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<Department>>

    suspend fun insert(vararg department: Department)

    suspend fun update(vararg department: Department)

    suspend fun delete(vararg department: Department)
}
