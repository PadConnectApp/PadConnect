/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */


package io.github.padconnect.utils.settings

import android.content.Context
import android.util.Log
import io.github.padconnect.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class ConfigModel(
    // Setup
    val initialSetupFinished: Boolean = BuildConfig.DEBUG,
    // Advanced Settings
    // (Core)
    val inputUpdateRate: Int = 500,
    val enableRumble: Boolean = true,
    // (Display)
    val showLatency: Boolean = true,
    val orientationMode: Int = 0,
    // Theme Settings
    val themeMode: Int = 0
)

object GlobalConfig {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private const val LOG_TAG = "GlobalConfig"

    private lateinit var configFile: File
    private var config: ConfigModel = ConfigModel()

    private val _configFlow = MutableStateFlow(ConfigModel())
    val configFlow: StateFlow<ConfigModel> = _configFlow.asStateFlow()

    val themeMode = configFlow.map { it.themeMode }.distinctUntilChanged()
    val enableRumbleFlow = configFlow.map { it.enableRumble }.distinctUntilChanged()
    val showLatencyFlow = configFlow.map { it.showLatency }.distinctUntilChanged()

    val orientationModeFlow = configFlow.map { it.orientationMode }.distinctUntilChanged()

    fun init(context: Context) {
        configFile = File(context.getExternalFilesDir(null), "config.json")

        if (configFile.exists()) {
            val content = configFile.readText()
            config = json.decodeFromString(content)
            _configFlow.value = config
        } else {
            save()
        }

        Log.i(LOG_TAG, "Initialized")
    }

    private fun save() {
        configFile.writeText(json.encodeToString(config))
        _configFlow.value = config
    }

    // Setup
    object INITIAL_SETUP_FINISHED {
        var boolean: Boolean
            get() = config.initialSetupFinished
            set(value) {
                config = config.copy(initialSetupFinished = value)
                save()
            }
    }

    // Advanced Settings
    // (Core)
    object INPUT_UPDATE_RATE {
        var int: Int
            get() = config.inputUpdateRate
            set(value) {
                config = config.copy(inputUpdateRate = value)
                save()
            }
    }

    object ENABLE_RUMBLE {
        var boolean: Boolean
            get() = config.enableRumble
            set(value) {
                config = config.copy(enableRumble = value)
                save()
            }
    }

    // (Display)
    object SHOW_LATENCY {
        var boolean: Boolean
            get() = config.showLatency
            set(value) {
                config = config.copy(showLatency = value)
                save()
            }
    }

    object ORIENTATION_MODE {
        var int: Int
            get() = config.orientationMode
            set(value) {
                config = config.copy(orientationMode = value)
                save()
            }
    }

    // Theme Settings
    object THEME_MODE {
        var int: Int
            get() = config.themeMode
            set(value) {
                config = config.copy(themeMode = value)
                save()
            }
    }
}