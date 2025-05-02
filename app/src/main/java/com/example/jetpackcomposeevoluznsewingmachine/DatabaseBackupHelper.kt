package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import android.util.Log
import java.io.File

object DatabaseBackupHelper {

    private const val DB_NAME = "machine_database"
    private const val BACKUP_FILE_NAME = "backup_machine_database.db"

    fun restoreDatabaseIfExists(context: Context) {
        try {
            val currentDB = File(context.getDatabasePath(DB_NAME).absolutePath)
            val backupDB = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)

            if (backupDB.exists() && !currentDB.exists()) {
                backupDB.copyTo(currentDB, overwrite = true)
                Log.d("DatabaseRestore", "Restore successful: ${currentDB.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("DatabaseRestore", "Restore failed", e)
        }
    }

    fun backupDatabase(context: Context) {
        try {
            val currentDB = File(context.getDatabasePath(DB_NAME).absolutePath)
            val backupDB = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)

            if (currentDB.exists()) {
                currentDB.copyTo(backupDB, overwrite = true)
                Log.d("DatabaseBackup", "Backup successful: ${backupDB.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("DatabaseBackup", "Backup failed", e)
        }
    }
}
