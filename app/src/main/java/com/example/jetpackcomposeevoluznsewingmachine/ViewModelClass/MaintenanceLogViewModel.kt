package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass


class MaintenanceLogViewModel(application: Application):AndroidViewModel(application) {
    private val dao = DatabaseClass.getDatabase(application).maintenanceLogDao()

}