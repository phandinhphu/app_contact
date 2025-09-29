package com.example.myapplication.ui.screen.user

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.user.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<String>(replay = 0)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _searchResult = MutableStateFlow<List<User>>(emptyList())

    val uiEvent = _uiEvent.asSharedFlow()
    val users: StateFlow<List<User>> = _users.asStateFlow()
    val searchResult: StateFlow<List<User>> = _searchResult.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        val data = getUsersUseCase()
        _users.value = data
        _searchResult.value = data
    }

    fun addUser(name: String, phone: String) {
        val id = (_users.value.maxOfOrNull { it.id } ?: 0) + 1

        val nameResult = validateUserNameUseCase(name)
        val phoneResult = validatePhoneNumberUseCase(phone)

        if (!nameResult.successful) {
            viewModelScope.launch {
                _uiEvent.emit(nameResult.errorMessage ?: "Lỗi tên")
            }
            return
        }

        if (!phoneResult.successful) {
            viewModelScope.launch {
                _uiEvent.emit(phoneResult.errorMessage ?: "Lỗi số điện thoại")
            }
            return
        }

        val user = User(id, name, phone)
        addUserUseCase(user)
        loadUsers()

        viewModelScope.launch {
            _uiEvent.emit("Thêm thành công")
        }
    }

    fun updateUser(user: User) {

        val nameResult = validateUserNameUseCase(user.name)
        val phoneResult = validatePhoneNumberUseCase(user.phone)

        if (!nameResult.successful) {
            viewModelScope.launch {
                _uiEvent.emit(nameResult.errorMessage ?: "Lỗi tên")
            }
            return
        }

        if (!phoneResult.successful) {
            viewModelScope.launch {
                _uiEvent.emit(phoneResult.errorMessage ?: "Lỗi số điện thoại")
            }
            return
        }

        updateUserUseCase(user)
        loadUsers()

        viewModelScope.launch {
            _uiEvent.emit("Sửa thành công")
        }
    }

    fun deleteUser(user: User) {
        deleteUserUseCase(user)
        loadUsers()

        viewModelScope.launch {
            _uiEvent.emit("Xóa thành công")
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            val currentUsers = _users.value
            _searchResult.value = if (query.isBlank()) {
                currentUsers
            } else {
                currentUsers.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.phone.contains(query)
                }
            }
        }
    }

    fun getUserById(id: Int?): User? {
        return id?.let { userId ->
            _users.value.find { it.id == userId }
        }
    }
}
