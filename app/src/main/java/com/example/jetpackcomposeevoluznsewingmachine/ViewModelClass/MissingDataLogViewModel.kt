package com.example.jetpackcomposeevoluznsewingmachine.ViewModelClass

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeevoluznsewingmachine.DatabaseClass
import com.example.jetpackcomposeevoluznsewingmachine.TableClass.MissingDataLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MissingDataLogViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseClass.getDatabase(application).missingDataLogDao()

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // StateFlows for observable metrics
    private val _totalDowntime = MutableStateFlow(0)
    val totalDowntime: StateFlow<Int> = _totalDowntime

    private val _mttr = MutableStateFlow(0f)
    val mttr: StateFlow<Float> = _mttr

    private val _mtbf = MutableStateFlow(0f)
    val mtbf: StateFlow<Float> = _mtbf

    private val _predictionText = MutableStateFlow("Loading...")
    val predictionText: StateFlow<String> = _predictionText

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.toMillis(): Long {
        return LocalDateTime.parse(this, formatter)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTimestamps(logs: List<MissingDataLog>): List<Pair<Long, Long>> {
        if (logs.isEmpty()) return emptyList()

        val sorted = logs.map { it.start.toMillis() to it.end.toMillis() }.sortedBy { it.first }
        val result = mutableListOf<Pair<Long, Long>>()

        var currentStart = sorted[0].first
        var currentEnd = sorted[0].second

        for (i in 1 until sorted.size) {
            val (nextStart, nextEnd) = sorted[i]
            if ((nextStart - currentEnd) <= 5 * 60 * 1000) { // 5-minute gap
                currentEnd = maxOf(currentEnd, nextEnd)
            } else {
                result.add(currentStart to currentEnd)
                currentStart = nextStart
                currentEnd = nextEnd
            }
        }

        result.add(currentStart to currentEnd)
        return result
    }


    private fun calculateTotalDowntime(groups: List<Pair<Long, Long>>): Int {
        return groups.sumOf { (start, end) -> ((end - start) / 60000).toInt() }
    }

    private fun calculateMTTR(groups: List<Pair<Long, Long>>): Float {
        if (groups.isEmpty()) return 0f
        val totalRepairTime = groups.sumOf { (start, end) -> ((end - start) / 60000).toInt() }
        return totalRepairTime.toFloat() / groups.size
    }

    private fun calculateMTBF(groups: List<Pair<Long, Long>>, shiftDurationMin: Int): Float {
        return if (groups.isNotEmpty()) {
            shiftDurationMin.toFloat() / groups.size
        } else 0f
    }

    private fun predictNextBreakdown(groups: List<Pair<Long, Long>>): String {
        if (groups.size < 2) return "Not enough data"
        val starts = groups.map { it.first }.sorted()
        val gaps = starts.zipWithNext().map { (a, b) -> (b - a) / (1000 * 60 * 60 * 24) } // in days
        val avgGap = gaps.average().toInt()
        return "Next Breakdown Expected In: $avgGap days"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun computeMetrics(start: String, end: String) {
        viewModelScope.launch {
            val logs = dao.getLogsBetween(start, end)

            val groups = groupTimestamps(logs)
            val downtimeMin = calculateTotalDowntime(groups)
            val mttrValue = calculateMTTR(groups)
            val mtbfValue = calculateMTBF(groups, shiftDurationMin = 480)
            val prediction = predictNextBreakdown(groups)

            _totalDowntime.value = downtimeMin
            _mttr.value = mttrValue
            _mtbf.value = mtbfValue
            _predictionText.value = prediction
        }
    }


}
