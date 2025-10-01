package com.example.myapplication.domain.usecase.user

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUserCase (private val userRepository: UserRepository) {
    operator fun invoke(query: String): Flow<List<User>> = userRepository.searchUsers(query)
}