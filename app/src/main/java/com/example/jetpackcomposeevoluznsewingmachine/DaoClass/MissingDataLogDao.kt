package com.example.jetpackcomposeevoluznsewingmachine.DaoClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MissingDataLog

@Dao
interface MissingDataLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: MissingDataLog)

    @Query("SELECT * FROM missing_data_log ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<MissingDataLog>
}