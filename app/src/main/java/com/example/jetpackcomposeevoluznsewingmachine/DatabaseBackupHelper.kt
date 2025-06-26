package com.example.jetpackcomposeevoluznsewingmachine


import android.content.Context
import android.net.Uri
import android.os.Environment

import android.util.Log
import android.widget.Toast
import com.example.jetpackcomposeevoluznsewingmachine.Screens.getMachineDataAsCsv
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MachineData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader

import java.io.File
import java.io.InputStreamReader


object DatabaseBackupHelper {

    private const val DB_NAME = "machine_database"
    private const val BACKUP_FILE_NAME = "machine_database_backup.csv"

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

    fun restoreCsvFromDownloads(context:Context){
        val downloadDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val csvFile=File(downloadDir,"machine_database_backup.csv")
        if(csvFile.exists()){
            val uri = Uri.fromFile(csvFile)
            importCsvFromUri(context, uri)
        }


    }

    private fun importCsvFromUri(context: Context, uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val lines = reader.readLines().drop(1) // Skip header line

                    val dao = DatabaseClass.getDatabase(context).machineDataDao()

                    for (line in lines) {
                        val columns = line.split(",")
                        if (columns.size >= 10) {
                            val data = columns[4].toDoubleOrNull()?.let {
                                columns[6].toIntOrNull()?.let { it1 ->
                                    columns[9].toIntOrNull()?.let { it2 ->
                                        MachineData(
                                            id = 0, // Let Room auto-generate if you use autoPrimaryKey
                                            dateTime = columns[1].removePrefix("=\"").removeSuffix("\""),
                                            runtime = columns[2].toIntOrNull() ?: 0,
                                            idleTime = columns[3].toIntOrNull() ?: 0,
                                            temperature = it,
                                            vibration = it,
                                            oilLevel = it1,
                                            pushBackCount = columns[7].toIntOrNull() ?: 0,
                                            stitchCount = columns[8].toIntOrNull() ?: 0,
                                            bobbinThread = it2
                                        )
                                    }
                                }
                            }
                            if (data != null) {
                                dao.insert(data)
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "CSV restored successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "CSV import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    fun exportCsvToDownloads(context: Context) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val csvContext = getMachineDataAsCsv(context)

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val csvFile = File(downloadsDir, "machine_data_backup.csv")

                csvFile.writeText(csvContext)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "CSV backup saved to Downloads", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("CSVBackup", "Export failed", e)
        }
    }


}
