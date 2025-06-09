package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass

class MissingDataLogViewMode(application: Application): AndroidViewModel(application) {
    private val dao = DatabaseClass.getDatabase(application).missingDataLogDao()
}