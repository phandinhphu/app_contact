package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers() : Flow<List<User>>
    suspend fun getUserById(id: Int): User?
    fun searchUsers(query: String) : Flow<List<User>>
    suspend fun addUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
}