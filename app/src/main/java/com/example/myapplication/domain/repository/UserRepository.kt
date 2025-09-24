package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User

interface UserRepository {
    fun getUsers() : List<User>
    fun getUserById(id: Int): User?
    fun searchUsers(query: String) : List<User>
    fun addUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(user: User)
}