package com.example.myapplication.di

import com.example.myapplication.data.local.dao.UserDao
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
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideAddUserUseCase(repository: UserRepository) = AddUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(repository: UserRepository) = GetUserByIdUserCase(repository)

    @Provides
    @Singleton
    fun provideSearchUsersUseCase(repository: UserRepository) = SearchUsersUserCase(repository)

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