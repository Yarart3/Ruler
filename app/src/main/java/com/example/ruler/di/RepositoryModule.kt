package com.example.ruler.di

import com.example.ruler.data.repository.AccessLogRepositoryImpl
import com.example.ruler.data.repository.HotelRepositoryImpl
import com.example.ruler.data.repository.LocalHotelRepositoryImpl
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.data.repository.UserRepositoryImpl
import com.example.ruler.data.repository.AuthRepositoryImpl
import com.example.ruler.domain.AccessLogRepository
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.LocalHotelRepository
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
    abstract fun bindAccessLogRepository(
        repository: AccessLogRepositoryImpl
    ): AccessLogRepository

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

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        repository: HotelRepositoryImpl
    ): HotelRepository

    @Binds
    @Singleton
    abstract fun bindLocalHotelRepository(
        repository: LocalHotelRepositoryImpl
    ): LocalHotelRepository
}
