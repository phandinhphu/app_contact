package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    private val users = mutableListOf<User>()

    override fun getUsers(): List<User> {
        return users
    }

    override fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override fun addUser(user: User) {
        users.add(user)
    }

    override fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
        }
    }

    override fun deleteUser(user: User) {
        users.remove(user)
    }

    override fun searchUsers(query: String): List<User> {
        if (query.isBlank()) {
            return users
        }

        return users.filter { it.name.contains(query, ignoreCase = true)
                || it.phone.contains(query, ignoreCase = true)}
    }
}