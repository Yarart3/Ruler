package com.example.ruler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ruler.data.local.entity.AccessLogEntity

@Dao
interface AccessLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AccessLogEntity)

    @Query(
        """
        SELECT * FROM access_logs
        WHERE user_id = :userId
        ORDER BY occurred_at_epoch_millis ASC, log_id ASC
        """
    )
    suspend fun getLogsByUserId(userId: String): List<AccessLogEntity>

    @Query("SELECT * FROM access_logs ORDER BY occurred_at_epoch_millis ASC, log_id ASC")
    suspend fun getAllLogs(): List<AccessLogEntity>
}
