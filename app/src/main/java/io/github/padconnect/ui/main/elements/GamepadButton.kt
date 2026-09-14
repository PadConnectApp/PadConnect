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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.padconnect.R
import io.github.padconnect.utils.ButtonElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamepadButton(
    modifier: Modifier,
    button: ButtonElement,
    screenWidth: Dp,
    screenHeight: Dp,
    buttonBounds: MutableMap<ButtonElement, Rect>,
    isPressed: Boolean = false,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onUpdate: (ButtonElement) -> Unit,
    isEditMode: Boolean
) {
    val density = LocalDensity.current

    val screenWidthPx = with(density) { screenWidth.toPx() }
    val screenHeightPx = with(density) { screenHeight.toPx() }

    val latestButton by rememberUpdatedState(button)

    var localX by remember(button.id) {
        mutableFloatStateOf(button.x)
    }

    var localY by remember(button.id) {
        mutableFloatStateOf(button.y)
    }

    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(button.x, button.y) {
        if (!isDragging) {
            localX = button.x
            localY = button.y
        }
    }

    val sizeDp = screenWidth * button.size
    val sizePx = screenWidthPx * button.size

    val xPx = screenWidthPx * localX - sizePx / 2f
    val yPx = screenHeightPx * localY - sizePx / 2f

    buttonBounds[button] = Rect(
        xPx,
        yPx,
        xPx + sizePx,
        yPx + sizePx
    )

    Box(
        modifier = modifier
            .offset(
                x = screenWidth * localX - sizeDp / 2,
                y = screenHeight * localY - sizeDp / 2
            )
            .size(sizeDp)
            .graphicsLayer {
                alpha = button.opacity
            }
            .background(
                when {
                    !button.enabled -> Color.White.copy(alpha = 0.05f)
                    !isPressed -> Color.White.copy(alpha = 0.3f)
                    else -> Color.Transparent
                },
                CircleShape
            )
            .border(
                width = when {
                    isSelected -> 2.dp
                    !button.enabled -> 1.dp
                    else -> 0.dp
                },

                color = when {
                    isSelected -> Color.Cyan
                    !button.enabled -> Color.White.copy(alpha = 0.25f)
                    else -> Color.Transparent
                },

                shape = CircleShape
            )
            .visible(if (!isEditMode) button.enabled else true)
            .pointerInput(isEditMode) {
                if (!isEditMode) return@pointerInput

                detectTapGestures(
                    onTap = {
                        onSelect()
                    }
                )
            }
            .pointerInput(isEditMode, button.enabled) {
                if (!isEditMode || !button.enabled) return@pointerInput

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
                            latestButton.copy(
                                x = localX,
                                y = localY
                            )
                        )
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.graphicsLayer {
                alpha = if (button.enabled) button.opacity else 0.35f
            }
        ) {
            GamepadButtonLabel(button.key.name)
        }
    }
}

@Composable
fun GamepadButtonLabel(keyName: String) {
    when (keyName) {
        "A" -> Text("A", style = labelStyle(), color = Color.White)
        "B" -> Text("B", style = labelStyle(), color = Color.White)
        "X" -> Text("X", style = labelStyle(), color = Color.White)
        "Y" -> Text("Y", style = labelStyle(), color = Color.White)

        "LB" -> Text("LB", style = smallLabelStyle(), color = Color.White)
        "RB" -> Text("RB", style = smallLabelStyle(), color = Color.White)

        "START" -> Icon(
            painter = painterResource(R.drawable.ic_play_arrow),
            tint = Color.White,
            contentDescription = "Start"
        )

        "SELECT" -> Icon(
            painter = painterResource(R.drawable.ic_menu),
            tint = Color.White,
            contentDescription = "Select"
        )

        else -> Text(keyName, style = smallLabelStyle(), color = Color.White)
    }
}

@Composable
private fun labelStyle() = MaterialTheme.typography.titleLarge.copy(
    fontWeight = FontWeight.Bold
)

@Composable
private fun smallLabelStyle() = MaterialTheme.typography.labelMedium