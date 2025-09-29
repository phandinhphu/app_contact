package com.example.myapplication.domain.usecase.user

import javax.inject.Inject

class ValidateUserNameUseCase @Inject constructor() {
    operator fun invoke(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(false, "Tên không được để trống")
        }
        if (name.length < 3) {
            return ValidationResult(false, "Tên phải có ít nhất 3 ký tự")
        }
        return ValidationResult(true)
    }
}

class ValidatePhoneNumberUseCase @Inject constructor() {
    operator fun invoke(phone: String): ValidationResult {
        val regex = Regex("^\\d{10}$")
        if (!regex.matches(phone)) {
            return ValidationResult(false, "Số điện thoại không hợp lệ")
        }
        return ValidationResult(true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)