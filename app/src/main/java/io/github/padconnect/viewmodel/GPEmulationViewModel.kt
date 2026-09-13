/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */


package io.github.padconnect.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.padconnect.dialogs.AlertDialogQueue
import io.github.padconnect.dialogs.AppDialog
import io.github.padconnect.transport.TransportManager
import io.github.padconnect.utils.DiscoverySender
import io.github.padconnect.utils.DiscoverySender.buildClientFeatures
import io.github.padconnect.utils.settings.GlobalConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class GPEmulationViewModel : ViewModel() {
    private val _lastLatency = MutableStateFlow<Double?>(null)
    val lastLatency: StateFlow<Double?> = _lastLatency

    private val _isTransportConnected = MutableStateFlow(false)
    val isTransportConnected: StateFlow<Boolean> = _isTransportConnected

    private val _isReceiverActive = MutableStateFlow(false)
    val isReceiverActive: StateFlow<Boolean> = _isReceiverActive

    private var lastUiUpdate = 0L
    private val uiIntervalNs = 1000_000_000L // 1000ms = 1Hz

    var transport: TransportManager? = null

    val onLatencyStatsReceive: (Double) -> Unit = { latency ->
        val now = System.nanoTime()
        if (now - lastUiUpdate > uiIntervalNs) {
            lastUiUpdate = now
            _lastLatency.value = latency
        }
    }

    private var receiverJob: Job? = null
    private var searchJob: Job? = null

    companion object {
        private const val LOG_TAG = "GPEmulationViewModel"
    }

    init {
        Log.i(LOG_TAG, "Initialized")
        searchReceiver()
        Log.i(LOG_TAG, "Searching Receiver...")
        observeReceiverState()
        observeFeatures()
    }

    fun searchReceiver() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            Log.i(LOG_TAG, "Searching Receiver...")
            while (isActive && !_isTransportConnected.value) {
                val result = DiscoverySender.discoverReceiver(
                    buildClientFeatures(
                        enableRumble = GlobalConfig.ENABLE_RUMBLE.boolean,
                        showLatency = true // important to show receiver status
                    )
                )

                if (result != null && result.host != null) {
                    Log.i(LOG_TAG, "DISCOVERED: ${result.host}, ${result.port}, features:${result.features}, agreedVersion:${result.agreedVersion}")

                    if (result.agreedVersion < DiscoverySender.MIN_SUPPORTED_VERSION) {
                        withContext(Dispatchers.Main) {
                            AlertDialogQueue.show(
                                AppDialog.Message(
                                    title = "Receiver Update Required",
                                    message = "This receiver version is not supported anymore.\n\nPlease update the receiver on your PC."
                                )
                            )
                        }
                        break
                    }

                    transport = TransportManager(result.host, result.port, onLatencyStatsReceive)
                    _isTransportConnected.value = true
                    transport!!.start()
                    Log.i(LOG_TAG, "Successfully connected")
                    break
                }
            }
        }
    }

    private fun observeReceiverState() {
        receiverJob?.cancel()
        receiverJob = viewModelScope.launch {
            while (isActive) {
                val active = transport?.isReceiverActive() == true

                if (_isReceiverActive.value != active) {
                    _isReceiverActive.value = active

                    if (!active && _isTransportConnected.value) {
                        Log.i(LOG_TAG, "Receiver lost! Tearing down and restarting discovery...")
                        transport?.stop()
                        transport = null
                        _isTransportConnected.value = false
                        searchReceiver()
                    }
                }

                delay(500.milliseconds)
            }
        }
    }

    private fun observeFeatures() {
        viewModelScope.launch {
            GlobalConfig.enableRumbleFlow
                .distinctUntilChanged()
                .collect { _ ->
                    if (_isTransportConnected.value) transport?.stop()
                }
        }
    }

    override fun onCleared() {
        searchJob?.cancel()
        receiverJob?.cancel()
        transport?.stop()
    }
}