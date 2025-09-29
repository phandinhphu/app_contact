package com.example.myapplication.di

import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.domain.repository.UserRepository
import com.example.myapplication.domain.usecase.user.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    @Singleton
    fun provideAddUserUseCase(repository: UserRepository) = AddUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUsersUseCase(repository: UserRepository) = GetUsersUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateUserUseCase(repository: UserRepository) = UpdateUserUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteUserUseCase(repository: UserRepository) = DeleteUserUseCase(repository)
}