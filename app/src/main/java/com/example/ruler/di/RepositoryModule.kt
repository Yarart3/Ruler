package com.example.ruler.di

import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.data.repository.AuthRepositoryImpl
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.TripRepository
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
}
