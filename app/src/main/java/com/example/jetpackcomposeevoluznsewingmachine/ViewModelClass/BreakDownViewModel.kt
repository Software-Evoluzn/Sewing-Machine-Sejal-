package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.app.Application
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.BreakDownReasonTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher


class BreakDownViewModel(application:Application):AndroidViewModel(application) {
    private val dao = DatabaseClass.getDatabase(application).breakDownReasonDao()

    private val _saveSuccess = mutableStateOf(false)
    val saveSuccess: State<Boolean> get() = _saveSuccess



   fun saveSelectedReason(selectedReasons:List<String>,downtime: String,
                          mttr: String,
                          mtbf: String,
                          prediction: String){
       viewModelScope.launch(Dispatchers.IO){
           selectedReasons.forEach {reason->
               dao.insert(
                   BreakDownReasonTable(
                       reasons = reason,
                       downtime = downtime,
                       mttr = mttr,
                       mtbf = mtbf,
                       prediction = prediction
                   )
               )

           }

           //notify UI after saving in database
           withContext(Dispatchers.Main){
               _saveSuccess.value=true
           }

       }

   }
    fun resetSuccessFlag(){
        _saveSuccess.value=false
    }


    suspend fun getAllReasons():List<BreakDownReasonTable> = dao.getAll()

}