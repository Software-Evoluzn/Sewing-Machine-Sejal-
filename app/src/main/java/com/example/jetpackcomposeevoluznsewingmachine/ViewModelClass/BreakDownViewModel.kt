package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.BreakDownReasonTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class BreakDownViewModel(application:Application):AndroidViewModel(application) {
    private val dao = DatabaseClass.getDatabase(application).breakDownReasonDao()

   fun saveSelectedReason(selectedReasons:List<String>){
       viewModelScope.launch(Dispatchers.IO){
           selectedReasons.forEach {reason->
               dao.insert(BreakDownReasonTable(reasons = reason))

           }
       }

   }

    suspend fun getAllReasons():List<BreakDownReasonTable> = dao.getAll()

}