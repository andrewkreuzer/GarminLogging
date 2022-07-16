package com.example.garminlogging.garmin

import android.util.Log
import com.example.garminlogging.GarminConnect
import com.garmin.android.connectiq.ConnectIQ
import javax.inject.Inject

class ConnectIQListener @Inject constructor(
    private val garminConnect: GarminConnect
) : ConnectIQ.ConnectIQListener {

    override fun onSdkReady() {
        val deviceResult = garminConnect.getDevices()
        deviceResult.onSuccess {
            garminConnect.device.postValue(it)
            garminConnect.registerDeviceEvents(it)
        }
        deviceResult.onFailure {
            Log.i("ERROR", "ConnectIqListener->onSdkReady()->onFailure: $it")
        }
    }

    override fun onSdkShutDown() {
        TODO("Not yet implemented")
    }

    override fun onInitializeError(status: ConnectIQ.IQSdkErrorStatus?) {
        Log.i(
            "ERROR",
            "ConnectIQListener->onInitializationError: Garmin SDK initialization error: $status"
        )
    }
}

