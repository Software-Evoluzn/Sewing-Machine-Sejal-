package com.example.jetpackcomposeevoluznsewingmachine.DaoClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.BreakDownReasonTable

@Dao
interface BreakDownReasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reason:BreakDownReasonTable)


    @Query("select * from BreakDown_Reasons order by timestamp DESC")
    suspend fun getAll():List<BreakDownReasonTable>
}