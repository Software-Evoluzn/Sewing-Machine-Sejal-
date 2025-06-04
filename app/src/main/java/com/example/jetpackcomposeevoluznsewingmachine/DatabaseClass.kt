package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jetpackcomposeevoluznsewingmachine.DaoClass.MachineDataDao
import com.example.jetpackcomposeevoluznsewingmachine.DaoClass.MaintenanceLogDao
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MachineData


@Database(entities = [MachineData::class], version = 7)
abstract class DatabaseClass : RoomDatabase() {

    abstract fun machineDataDao(): MachineDataDao
    abstract fun maintenanceLogDao():MaintenanceLogDao


    companion object {
        @Volatile
        private var INSTANCE: DatabaseClass? = null

        private val MIGRATION_3_4= object: Migration(3,4){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE machine_data ADD COLUMN pushBackCount INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5=object:Migration(4,5){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE machine_data ADD COLUMN stitchCount INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_5_6=object:Migration(5,6){
            override fun migrate(db: SupportSQLiteDatabase) {
               db.execSQL("ALTER TABLE machine_data ADD COLUMN bobbinThread INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_6_7 = object:Migration(6,7){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS maintenance_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                maintenance_time INTEGER NOT NULL
            )
                """.trimIndent())
                println("table create successfully")
            }
        }






        fun getDatabase(context: Context): DatabaseClass {
            return INSTANCE ?: synchronized(this) {

                DatabaseBackupHelper.restoreDatabaseIfExists(context)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseClass::class.java,
                    "machine_database"
                ).addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                INSTANCE = instance


                instance
            }
        }
    }
}