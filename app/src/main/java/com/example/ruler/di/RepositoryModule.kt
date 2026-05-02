package com.example.ruler.di

import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.data.repository.UserRepositoryImpl
import com.example.ruler.data.repository.AuthRepositoryImpl
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.TripRepository
import com.example.ruler.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        repository: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        repository: UserRepositoryImpl
    ): UserRepository
}
