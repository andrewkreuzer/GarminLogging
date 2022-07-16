package com.example.garminlogging

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.garminlogging.data.LogData
import com.example.garminlogging.garmin.ConnectIQAppListener
import com.example.garminlogging.garmin.ConnectIQListener
import com.example.garminlogging.garmin.GarminLog
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class GarminConnect @Inject constructor(
    @ActivityContext private val context: Context,
    private var logData: LogData
) {
    private var connectIQ: ConnectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.TETHERED)
    lateinit var app: IQApp

    var device: MutableLiveData<IQDevice> = MutableLiveData(null)

    var isLogging = false

    init {

        try {
            connectIQ.initialize(context, true, ConnectIQListener(this))
        } catch (ex: Exception) {
            Log.i("ERROR", "GarminConnect->init:  ${ex.message}")
        }
    }

    fun getDevices(): Result<IQDevice> {
        val paired = connectIQ.knownDevices

        if (paired != null && paired.size > 0) {
            for (d in paired) {
                Log.i("INFO", "GarminConnect->getDevices(): Garmin device: ${d.friendlyName}")
                return Result.success(d)
            }
        }
        return Result.failure(Throwable("No device found"))
    }

    fun registerDeviceEvents(device: IQDevice) {
        connectIQ.registerForDeviceEvents(
            device
        ) { device, newStatus ->
            Log.i(
                "INFO",
                "GarminConnect->registerDeviceEvents(): Device: $device, Status: $newStatus"
            )
            getApplicationInfo()
        }
    }

    private fun getApplicationInfo() {
        val trainingAppID = "B181DB00226B11E48C211234200C9A66"
        Log.i(
            "INFO",
            "GarminConnect->getApplicationInfo(): Device: $device, AppId: $trainingAppID"
        )
        connectIQ.getApplicationInfo(trainingAppID, device.value, ConnectIQAppListener(this))
    }

    fun registerMessageReceiver(): Boolean {
        return try {
            connectIQ.registerForAppEvents(
                device.value, app
            ) { device, app, messageData, status ->
                if (status == IQMessageStatus.SUCCESS) {
                    isLogging = true
                    Log.i(
                        "MESSAGE",
                        "GarminConnect->registerMessageReceiver(): Device: $device, App: $app, DeviceMessage: $messageData"
                    )
                    logData.addLog(GarminLog( messageData[0].toString()))
                }
            }
            true
        } catch (ex: Exception) {
            Log.i("ERROR", "GarminConnect->registerMessageReceiver(): $ex")
            false
        }
    }
    fun unregisterMessageReceiver(): Boolean {
        return try {
            connectIQ.unregisterForApplicationEvents(device.value, app)
            isLogging = false
            true
        } catch (ex: Exception) {
            Log.i("ERROR", ex.toString())
            false
        }
    }

    fun sendMessage(message: String) {
        this.connectIQ.sendMessage(
            device.value, app, message
        ) { device, app, iqMessageStatus ->
            Log.i(
                "INFO",
                "GarminConnect->sendMessage(): Device: $device, App: $app, Status: $iqMessageStatus, Message: $message"
            )
        }
    }

}
