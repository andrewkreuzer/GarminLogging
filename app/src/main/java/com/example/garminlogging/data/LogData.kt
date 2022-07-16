package com.example.garminlogging.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.garminlogging.garmin.GarminLog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogData @Inject constructor() {
    private val initialLogList: List<GarminLog> = listOf(GarminLog("No Logs"))
    private val logListLiveData: MutableLiveData<List<GarminLog>> = MutableLiveData(initialLogList)

    fun addLog(garminLog: GarminLog) {
        val currentList = logListLiveData.value
        if (currentList == null || currentList.last().message == "No Logs") {
            logListLiveData.postValue(listOf(garminLog))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(getLogListLength(), garminLog)
            logListLiveData.postValue(updatedList)
        }
    }

    fun removeLog(garminLog: GarminLog) {
        val currentList = logListLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(garminLog)
            logListLiveData.postValue(updatedList)
        }
    }

    fun clear() {
        val currentList = logListLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.clear()
            logListLiveData.postValue(updatedList)
        }
    }

    fun getLogList(): LiveData<List<GarminLog>> {
        return logListLiveData
    }

    private fun getLogListLength(): Int {
        val length = logListLiveData.value?.size
        if (length != null) return length

        return 0
    }
}