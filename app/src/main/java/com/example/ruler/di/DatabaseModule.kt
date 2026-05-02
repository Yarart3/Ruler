package com.example.ruler.di

import android.content.Context
import androidx.room.Room
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.local.dao.AccessLogDao
import com.example.ruler.data.local.dao.ItineraryItemDao
import com.example.ruler.data.local.dao.TripDao
import com.example.ruler.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRulerDatabase(
        @ApplicationContext context: Context
    ): RulerDatabase {
        return Room.databaseBuilder(
            context,
            RulerDatabase::class.java,
            "ruler_database"
        ).addMigrations(
            RulerDatabase.MIGRATION_1_2,
            RulerDatabase.MIGRATION_2_3
        ).build()
    }

    @Provides
    fun provideAccessLogDao(database: RulerDatabase): AccessLogDao = database.accessLogDao()

    @Provides
    fun provideTripDao(database: RulerDatabase): TripDao = database.tripDao()

    @Provides
    fun provideItineraryItemDao(database: RulerDatabase): ItineraryItemDao =
        database.itineraryItemDao()

    @Provides
    fun provideUserDao(database: RulerDatabase): UserDao = database.userDao()
}
