/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.ui.main.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.padconnect.transport.TransportManager
import io.github.padconnect.utils.AnalogStickElement
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnalogStick(
    dpad: AnalogStickElement,
    transport: TransportManager?,
    screenWidth: Dp,
    screenHeight: Dp,
    controlPointers: MutableSet<PointerId>,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onUpdate: (AnalogStickElement) -> Unit = {}
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

    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    var activePointer by remember { mutableStateOf<PointerId?>(null) }

    Box(
        modifier = Modifier
            .offset(
                x = screenWidth * localX - sizeDp / 2,
                y = screenHeight * localY - sizeDp / 2
            )
            .size(sizeDp)
            .graphicsLayer { alpha = dpad.opacity }
            .background(
                when {
                    !dpad.enabled -> Color.White.copy(alpha = 0.05f)
                    else -> Color.Transparent
                },
                CircleShape
            )
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
                detectTapGestures(
                    onTap = { onSelect() }
                )
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
                        onUpdate(
                            latestDpad.copy(
                                x = localX,
                                y = localY
                            )
                        )
                    },
                    onDragCancel = {
                        isDragging = false
                    }
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
                                val center = Offset(radius, radius)
                                val delta = change.position - center

                                val dist = delta.getDistance()
                                val clamped =
                                    if (dist > radius) delta * (radius / dist)
                                    else delta

                                knobOffset = clamped

                                val x = (clamped.x / radius).coerceIn(-1f, 1f)
                                val y = (-clamped.y / radius).coerceIn(-1f, 1f)

                                transport?.setLeftAxis(x, y)
                            }

                            if (change.id == activePointer && change.changedToUp()) {
                                activePointer = null
                                controlPointers.remove(change.id)
                                knobOffset = Offset.Zero

                                transport?.setLeftAxis(0f, 0f)
                            }
                        }
                    }
                }
            }
    ) {
        AnalogStickVisual(knobOffset)
    }
}

@Composable
private fun AnalogStickVisual(knobOffset: Offset) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White.copy(alpha = 0.15f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        knobOffset.x.roundToInt(),
                        knobOffset.y.roundToInt()
                    )
                }
                .size(28.dp)
                .background(
                    Color.White.copy(alpha = 0.5f),
                    CircleShape
                )
        )
    }
}

