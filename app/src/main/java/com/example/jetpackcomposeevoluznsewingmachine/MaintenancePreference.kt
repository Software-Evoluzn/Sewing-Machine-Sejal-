package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context

class MaintenancePreference(context: Context) {
    val sharedPref=context.getSharedPreferences("maintenance_prefs",Context.MODE_PRIVATE)

       fun setLastMaintenanceTime(time:Long){
           sharedPref.edit().putLong("last_maintenance_time",time).apply()
       }

       fun getLastMaintenanceTime():Long{
           return sharedPref.getLong("last_maintenance_time",0L)


       }

}