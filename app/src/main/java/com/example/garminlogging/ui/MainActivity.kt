package com.example.garminlogging.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.garminlogging.*
import com.example.garminlogging.data.LogData
import com.example.garminlogging.data.LogListViewModel
import com.example.garminlogging.garmin.GarminLog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val logListViewModel: LogListViewModel by viewModels()
    private lateinit var logListRecyclerView: RecyclerView
    private lateinit var fab: View

    @Inject lateinit var garminConnect: GarminConnect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLogList()
        initializeFab()
    }

    private fun initializeLogList() {
        val logListAdapter = LogListAdapter()
        logListRecyclerView = findViewById(R.id.log_list)
        logListRecyclerView.visibility = View.INVISIBLE
        logListRecyclerView.adapter = logListAdapter
        logListViewModel.logLiveData.observe(this) {
            it?.let {
                logListAdapter.submitList(it as MutableList<GarminLog>)

                val layoutManager = logListRecyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() + 1 == logListAdapter.itemCount) {
                    logListRecyclerView.smoothScrollToPosition(logListAdapter.itemCount)
                }
            }
        }

        garminConnect.device.observe(this) {
            it?.let {
                Snackbar.make(findViewById(R.id.root), "New device found $it", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }
        }
    }

    private fun initializeFab() {
        fab = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            if (garminConnect.isLogging) {
                if (garminConnect.unregisterMessageReceiver()) {
                    Snackbar.make(view, "Stopped log collection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }

            } else {
                if (garminConnect.registerMessageReceiver()) {
                    logListRecyclerView.visibility = View.VISIBLE
                    Snackbar.make(view, "Starting log collection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                } else {
                    Snackbar.make(
                        view,
                        "Unable to start logs, is the device connected?",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null)
                        .show()
                }
            }
        }

        fab.setOnLongClickListener { view ->
            Snackbar.make(view, "Clearing Logs", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()

            logListViewModel.clearLogs()
            true
        }
    }
}