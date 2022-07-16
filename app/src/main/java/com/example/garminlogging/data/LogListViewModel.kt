package com.example.garminlogging.data

import androidx.lifecycle.ViewModel
import com.example.garminlogging.garmin.GarminLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogListViewModel @Inject constructor(
    private var logData: LogData
): ViewModel() {

    var logLiveData = logData.getLogList()
    fun insertLog(msg: String?) {
        if (msg == null) {
            return
        }
        val newLog = GarminLog(msg)
        logData.addLog(newLog)
    }

    fun clearLogs() {
        logData.clear()
    }
}