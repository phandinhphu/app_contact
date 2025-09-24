package com.example.myapplication.ui.screen.user

import androidx.lifecycle.*
import com.example.myapplication.domain.usecase.user.*

class UserViewModelFactory (
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(
                getUsersUseCase,
                addUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                validateUserNameUseCase,
                validatePhoneNumberUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}