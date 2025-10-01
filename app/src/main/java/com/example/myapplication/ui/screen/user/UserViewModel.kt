package com.example.myapplication.ui.screen.user

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
    private val getUserByIdUserCase: GetUserByIdUserCase,
    private val searchUsersUseCase: SearchUsersUserCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<String>(replay = 0)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _user = MutableStateFlow<User?>(null)
    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)

    val uiEvent = _uiEvent.asSharedFlow()
    val users: StateFlow<List<User>> = _users.asStateFlow()
    val user: StateFlow<User?> = _user.asStateFlow()
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        val data = getUsersUseCase()
        viewModelScope.launch {
            data.collect {
                _users.value = it
            }
        }
    }

    fun addUser(name: String, phone: String) {
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

        viewModelScope.launch {
            val user = User(name = name,phone = phone)
            addUserUseCase(user)
            loadUsers()

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

        viewModelScope.launch {
            updateUserUseCase(user)
            loadUsers()

            _uiEvent.emit("Sửa thành công")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            deleteUserUseCase(user)
            loadUsers()

            _uiEvent.emit("Xóa thành công")
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            searchUsersUseCase(query).collect {
                _users.value = it
            }
        }
    }

    fun getUserById(id: Int?) {
        viewModelScope.launch {
            val user = getUserByIdUserCase(id ?: 0)
            _userUiState.value = if (user != null) {
                UserUiState.Success(user)
            } else {
                UserUiState.Error("Không tìm thấy người dùng")
            }
        }
    }
}

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}