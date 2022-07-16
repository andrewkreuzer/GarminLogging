package com.example.garminlogging.garmin

import android.util.Log
import com.example.garminlogging.GarminConnect
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import javax.inject.Inject

class ConnectIQAppListener @Inject constructor(
    private val garminConnect: GarminConnect
) : ConnectIQ.IQApplicationInfoListener {

    override fun onApplicationInfoReceived(app: IQApp?) {
        Log.i(
            "INFO",
            "ConnectIQAppListener->onApplicationInfoReceived(): App ID: ${app?.applicationId}"
        )
        if (app != null) {
            garminConnect.app = app
        }
    }

    override fun onApplicationNotInstalled(error: String?) {
        Log.i("ERROR", "ConnectIQAppListener->onApplicationNotInstalled(): $error")
    }
}
