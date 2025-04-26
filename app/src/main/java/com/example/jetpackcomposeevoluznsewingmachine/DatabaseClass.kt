package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.MachineData


@Database(entities = [MachineData::class], version = 2, exportSchema = false)
abstract class DatabaseClass : RoomDatabase() {

    abstract fun machineDataDao(): MachineDataDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseClass? = null

        fun getDatabase(context: Context): DatabaseClass {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseClass::class.java,
                    "machine_database"
                ).fallbackToDestructiveMigration()  // 💥 This clears old schema and creates a new one
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
