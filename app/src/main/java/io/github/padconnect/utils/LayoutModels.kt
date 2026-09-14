/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ControllerLayout(
    val name: String,
    val elements: List<ControllerElement>
)

@Immutable
@Serializable
sealed class ControllerElement {
    abstract val id: String
    abstract val x: Float
    abstract val y: Float
    abstract val size: Float
    abstract val opacity: Float
    abstract val enabled: Boolean
}

@Immutable
@SuppressLint("UnsafeOptInUsageError")
@Serializable
@SerialName("button")
data class ButtonElement(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val size: Float,
    override val opacity: Float,
    override val enabled: Boolean = true,
    val key: GamepadKey
) : ControllerElement()

@Immutable
@SuppressLint("UnsafeOptInUsageError")
@Serializable
@SerialName("dpad")
data class DPadElement(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val size: Float,
    override val opacity: Float,
    override val enabled: Boolean = true,
    val upKey: GamepadKey = GamepadKey.DPAD_UP,
    val downKey: GamepadKey = GamepadKey.DPAD_DOWN,
    val leftKey: GamepadKey = GamepadKey.DPAD_LEFT,
    val rightKey: GamepadKey = GamepadKey.DPAD_RIGHT
) : ControllerElement()

@Immutable
@SuppressLint("UnsafeOptInUsageError")
@Serializable
@SerialName("analog_stick")
data class AnalogStickElement(
    override val id: String,
    override val x: Float,
    override val y: Float,
    override val size: Float,
    override val opacity: Float,
    override val enabled: Boolean = true
) : ControllerElement()

enum class GamepadKey(val id: Int) {
    A(0x1000), B(0x2000), X(0x4000), Y(0x8000), L3(0x0040), R3(0x0080), LT(7), RT(9), LB(0x0100), RB(0x0200), START(0x0010), SELECT(0x0020), DPAD_UP(0x0001), DPAD_DOWN(0x0002), DPAD_LEFT(0x0004), DPAD_RIGHT(0x0008)
}

