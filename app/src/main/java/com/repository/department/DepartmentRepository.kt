package com.repository.department

import com.domain.RepoResult
import com.domain.entities.Department
import kotlinx.coroutines.flow.Flow


interface DepartmentRepository {
    fun observeAll(
        name: String? = null
    ): Flow<List<Department>>

    suspend fun getAll(
        name: String? = null
    ): List<Department>

    suspend fun insert(vararg departments: Department): RepoResult<Unit>

    suspend fun update(vararg departments: Department): RepoResult<Unit>

    suspend fun delete(vararg departments: Department)
}
