package com.repository.department

import com.domain.RepoResult
import com.domain.entities.Department


interface DepartmentRepository {
    fun observeAll(
        name: String? = null
    ): kotlinx.coroutines.flow.Flow<List<Department>>

    suspend fun insert(vararg departments: Department): RepoResult<Unit>

    suspend fun update(vararg departments: Department): RepoResult<Unit>

    suspend fun delete(vararg departments: Department)
}
