package com.repository.user_role

import com.domain.entities.UserRoleEntity


interface UserRoleRepository {
    suspend fun getAll(): List<UserRoleEntity>

    suspend fun getByName(name: String): UserRoleEntity?
}