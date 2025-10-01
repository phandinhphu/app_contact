package com.example.myapplication.domain.usecase.user

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.UserRepository

class DeleteUserUseCase (private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User) = userRepository.deleteUser(user)
}