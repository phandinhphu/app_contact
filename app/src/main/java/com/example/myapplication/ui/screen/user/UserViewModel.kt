package com.example.myapplication.ui.screen.user

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.usecase.user.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<String>(replay = 0)
    private val _searchResult = MutableStateFlow<List<User>>(emptyList())

    val uiEvent = _uiEvent.asSharedFlow()
    val users = mutableStateListOf<User>()
    val searchResult: StateFlow<List<User>> = _searchResult

    init {
        users.addAll(getUsersUseCase())
        _searchResult.value = users
    }

    fun addUser(name: String, phone: String) {
        val id = (users.maxOfOrNull { it.id } ?: 0) + 1

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
        users.add(user)
        addUserUseCase(user)
        _searchResult.value = users.toList()

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
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
        }
        _searchResult.value = users.toList()

        viewModelScope.launch {
            _uiEvent.emit("Sửa thành công")
        }
    }

    fun deleteUser(user: User) {
        deleteUserUseCase(user)
        users.remove(user)
        _searchResult.value = users.toList()

        viewModelScope.launch {
            _uiEvent.emit("Xóa thành công")
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _searchResult.value = if (query.isBlank()) {
                users.toList() // nếu query rỗng → trả toàn bộ
            } else {
                users.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.phone.contains(query)
                }
            }
        }
    }

    fun getUserById(id: Int?): User? {
        return id?.let { users.find { it.id == id } }
    }
}
