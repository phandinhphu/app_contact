package com.example.myapplication.domain.usecase.user

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserByIdUserCase (private val userRepository: UserRepository) {
    suspend operator fun invoke(id: Int): User? = userRepository.getUserById(id)
}