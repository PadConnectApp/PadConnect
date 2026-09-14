/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.ui.main.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.padconnect.transport.TransportManager
import io.github.padconnect.utils.DPadElement
import io.github.padconnect.utils.GamepadKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DPad(
    dpad: DPadElement,
    transport: TransportManager?,
    screenWidth: Dp,
    screenHeight: Dp,
    controlPointers: MutableSet<PointerId>,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onUpdate: (DPadElement) -> Unit = {}
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeightPx = with(density) { screenHeight.toPx() }

    val sizeDp = screenWidth * dpad.size
    val sizePx = screenWidthPx * dpad.size
    val radius = sizePx / 2f

    val latestDpad by rememberUpdatedState(dpad)

    var localX by remember(dpad.id) { mutableFloatStateOf(dpad.x) }
    var localY by remember(dpad.id) { mutableFloatStateOf(dpad.y) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(dpad.x, dpad.y) {
        if (!isDragging) {
            localX = dpad.x
            localY = dpad.y
        }
    }

    var activePointer by remember { mutableStateOf<PointerId?>(null) }
    var pressedDirections by remember { mutableStateOf(setOf<GamepadKey>()) }

    fun updateDirections(offset: Offset) {
        val center = Offset(radius, radius)
        val delta = offset - center
        val dist = delta.getDistance()

        val deadZone = radius * 0.15f
        val newDirections = mutableSetOf<GamepadKey>()

        if (dist > deadZone) {
            val normX = delta.x / radius
            val normY = delta.y / radius

            if (normY < -0.3f) newDirections.add(dpad.upKey)
            if (normY > 0.3f) newDirections.add(dpad.downKey)
            if (normX < -0.3f) newDirections.add(dpad.leftKey)
            if (normX > 0.3f) newDirections.add(dpad.rightKey)
        }

        val released = pressedDirections - newDirections
        val pressed = newDirections - pressedDirections

        released.forEach { transport?.setButton(it.id, false) }
        pressed.forEach { transport?.setButton(it.id, true) }

        pressedDirections = newDirections
    }

    Box(
        modifier = Modifier
            .offset(
                x = screenWidth * localX - sizeDp / 2,
                y = screenHeight * localY - sizeDp / 2
            )
            .size(sizeDp)
            .graphicsLayer { alpha = dpad.opacity }
            .border(
                width = when {
                    isSelected -> 2.dp
                    !dpad.enabled -> 1.dp
                    else -> 0.dp
                },
                color = when {
                    isSelected -> Color.Cyan
                    !dpad.enabled -> Color.White.copy(alpha = 0.25f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .visible(if (!isEditMode) dpad.enabled else true)
            .pointerInput(isEditMode) {
                if (!isEditMode) return@pointerInput
                detectTapGestures(onTap = { onSelect() })
            }
            .pointerInput(isEditMode, dpad.enabled) {
                if (!isEditMode || !dpad.enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onSelect()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        localX = (localX + dragAmount.x / screenWidthPx).coerceIn(0f, 1f)
                        localY = (localY + dragAmount.y / screenHeightPx).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        isDragging = false
                        onUpdate(latestDpad.copy(x = localX, y = localY))
                    },
                    onDragCancel = { isDragging = false }
                )
            }
            .pointerInput(Unit) {
                if (isEditMode) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed && activePointer == null) {
                                activePointer = change.id
                                controlPointers.add(change.id)
                            }

                            if (change.id == activePointer && change.pressed) {
                                updateDirections(change.position)
                            }

                            if (change.id == activePointer && change.changedToUp()) {
                                activePointer = null
                                controlPointers.remove(change.id)
                                pressedDirections.forEach { transport?.setButton(it.id, false) }
                                pressedDirections = emptySet()
                            }
                        }
                    }
                }
            }
    ) {
        DPadVisual(pressedDirections = pressedDirections, dpad = dpad)
    }
}

@Composable
private fun DPadVisual(
    pressedDirections: Set<GamepadKey>,
    dpad: DPadElement
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val armWidth = w * 0.34f

            val path = Path().apply {
                // Top arm
                moveTo((w - armWidth) / 2, 0f)
                lineTo((w + armWidth) / 2, 0f)
                lineTo((w + armWidth) / 2, (h - armWidth) / 2)
                // Right arm
                lineTo(w, (h - armWidth) / 2)
                lineTo(w, (h + armWidth) / 2)
                lineTo((w + armWidth) / 2, (h + armWidth) / 2)
                // Bottom arm
                lineTo((w + armWidth) / 2, h)
                lineTo((w - armWidth) / 2, h)
                lineTo((w - armWidth) / 2, (h + armWidth) / 2)
                // Left arm
                lineTo(0f, (h + armWidth) / 2)
                lineTo(0f, (h - armWidth) / 2)
                lineTo((w - armWidth) / 2, (h - armWidth) / 2)
                close()
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.15f)
            )
        }

        DPadArrow(Alignment.TopCenter, pressedDirections.contains(dpad.upKey), 0f)
        DPadArrow(Alignment.BottomCenter, pressedDirections.contains(dpad.downKey), 180f)
        DPadArrow(Alignment.CenterStart, pressedDirections.contains(dpad.leftKey), 270f)
        DPadArrow(Alignment.CenterEnd, pressedDirections.contains(dpad.rightKey), 90f)
    }
}

@Composable
private fun BoxScope.DPadArrow(
    alignment: Alignment,
    isPressed: Boolean,
    rotationDegrees: Float
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .padding(16.dp)
            .size(10.dp)
            .rotate(rotationDegrees)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.7f)
                lineTo(size.width / 2f, size.height * 0.2f)
                lineTo(size.width, size.height * 0.7f)
            }

            drawPath(
                path = path,
                color = if (isPressed) Color.Cyan else Color.White.copy(alpha = 0.6f),
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}