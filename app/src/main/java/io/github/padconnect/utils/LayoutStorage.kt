/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.utils

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File

object LayoutStorage {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    private const val FILE_NAME = "layouts.json"

    fun load(context: Context): MutableList<ControllerLayout> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return mutableListOf()
        return json.decodeFromString(file.readText())
    }

    fun load(context: Context, name: String): ControllerLayout? {
        return load(context).firstOrNull { it.name == name }
    }

    fun save(context: Context, layouts: MutableList<ControllerLayout>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(layouts))
    }

    fun createDefault(name: String): ControllerLayout {
        return ControllerLayout(
            name = name,
            elements = listOf(
                ButtonElement(
                    id = "btn_a",
                    x = 0.78f,
                    y = 0.67f,
                    size = 0.09f,
                    opacity = 0.85f,
                    key = GamepadKey.A
                ),
                ButtonElement(
                    id = "btn_b",
                    x = 0.87f,
                    y = 0.55f,
                    size = 0.09f,
                    opacity = 0.85f,
                    key = GamepadKey.B
                ),
                ButtonElement(
                    id = "btn_x",
                    x = 0.69f,
                    y = 0.55f,
                    size = 0.09f,
                    opacity = 0.85f,
                    key = GamepadKey.X
                ),
                ButtonElement(
                    id = "btn_y",
                    x = 0.78f,
                    y = 0.45f,
                    size = 0.09f,
                    opacity = 0.85f,
                    key = GamepadKey.Y
                ),

                AnalogStickElement(
                    id = "analog_stick",
                    x = 0.22f,
                    y = 0.55f,
                    size = 0.18f,
                    opacity = 0.8f
                ),

                DPadElement(
                    id = "dpad",
                    x = 0.40f,
                    y = 0.73f,
                    size = 0.10f,
                    opacity = 0.8f
                ),

                ButtonElement(
                    id = "btn_lt",
                    x = 0.10f,
                    y = 0.15f,
                    size = 0.12f,
                    opacity = 0.7f,
                    key = GamepadKey.LT
                ),
                ButtonElement(
                    id = "btn_rt",
                    x = 0.90f,
                    y = 0.15f,
                    size = 0.12f,
                    opacity = 0.7f,
                    key = GamepadKey.RT
                ),

                ButtonElement(
                    id = "btn_lb",
                    x = 0.25f,
                    y = 0.18f,
                    size = 0.12f,
                    opacity = 0.7f,
                    key = GamepadKey.LB
                ),
                ButtonElement(
                    id = "btn_rb",
                    x = 0.75f,
                    y = 0.18f,
                    size = 0.12f,
                    opacity = 0.7f,
                    key = GamepadKey.RB
                ),

                ButtonElement(
                    id = "btn_l3",
                    x = 0.42f,
                    y = 0.25f,
                    size = 0.07f,
                    opacity = 0.6f,
                    key = GamepadKey.L3
                ),
                ButtonElement(
                    id = "btn_r3",
                    x = 0.58f,
                    y = 0.25f,
                    size = 0.07f,
                    opacity = 0.6f,
                    key = GamepadKey.R3
                ),

                ButtonElement(
                    id = "btn_select",
                    x = 0.42f,
                    y = 0.45f,
                    size = 0.07f,
                    opacity = 0.6f,
                    key = GamepadKey.SELECT
                ),
                ButtonElement(
                    id = "btn_start",
                    x = 0.58f,
                    y = 0.45f,
                    size = 0.07f,
                    opacity = 0.6f,
                    key = GamepadKey.START
                )
            )
        )
    }

    inline fun ControllerLayout.updateElement(
        id: String,
        update: (ControllerElement) -> ControllerElement
    ): ControllerLayout {
        return copy(
            elements = elements.map {
                if (it.id == id) update(it) else it
            }
        )
    }
}
