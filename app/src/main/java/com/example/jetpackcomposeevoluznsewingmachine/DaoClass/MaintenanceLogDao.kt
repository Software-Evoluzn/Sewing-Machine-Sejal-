package com.example.jetpackcomposeevoluznsewingmachine.DaoClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MaintenanceLog


@Dao
interface MaintenanceLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceLog(log: MaintenanceLog)


    @Query(
        """
         SELECT * FROM maintenance_log
         order by maintenance_time DESC LIMIT 1
    """
    )
    suspend fun getMaintenanceLog(): MaintenanceLog?

}