package com.example.myapplication.data.mapper

import com.example.myapplication.data.local.entity.UserEntity
import com.example.myapplication.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        phone = this.phone
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        phone = this.phone
    )
}