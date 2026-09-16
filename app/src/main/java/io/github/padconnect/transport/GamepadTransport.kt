/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.transport

data class GamepadState(
    var buttons: Int = 0,
    var lx: Short = 0,
    var ly: Short = 0,
    var rx: Short = 0,
    var ry: Short = 0,
    var lt: UByte = 0u,
    var rt: UByte = 0u
)


interface GamepadTransport {
    fun setButton(mask: Int, down: Boolean)
    fun setLeftAxis(x: Float, y: Float)
    fun setRightAxis(x: Float, y: Float)
    fun isAvailable(): Boolean
}
