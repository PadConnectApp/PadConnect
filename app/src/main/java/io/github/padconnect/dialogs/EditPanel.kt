/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.materialswitch.MaterialSwitch
import io.github.padconnect.R
import io.github.padconnect.utils.AnalogStickElement
import io.github.padconnect.utils.ButtonElement
import io.github.padconnect.utils.ControllerElement
import io.github.padconnect.utils.DPadElement
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPanel(
    element: ControllerElement?,
    allElements: List<ControllerElement>,
    onUpdate: (ControllerElement) -> Unit,
    onUpdateAll: (size: Float?, opacity: Float?, enabled: Boolean?) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember {
        mutableFloatStateOf(40f)
    }

    var offsetY by remember {
        mutableFloatStateOf(120f)
    }

    val isAllSelected = element == null
    val referenceElement = element ?: allElements.firstOrNull()

    val currentSize = referenceElement?.size ?: 0.15f
    val currentOpacity = referenceElement?.opacity ?: 0.5f
    val currentEnabled = referenceElement?.enabled ?: true

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    offsetX.roundToInt(),
                    offsetY.roundToInt()
                )
            }
            .width(260.dp)
            .background(
                Color.Black.copy(alpha = 0.92f),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(20.dp)
            )
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp
                        )
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = element?.id ?: "All",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_refresh),
                        contentDescription = "Reset Customizations",
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.08f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "≡",
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Size",
                    color = Color.White
                )

                Slider(
                    value = currentSize,
                    onValueChange = { value ->
                        if (isAllSelected) {
                            onUpdateAll(value, null, null)
                        } else {
                            element.let {
                                when (it) {
                                    is ButtonElement -> onUpdate(it.copy(size = value))
                                    is AnalogStickElement -> onUpdate(it.copy(size = value))
                                    is DPadElement -> onUpdate(it.copy(size = value))
                                }
                            }
                        }
                    },
                    valueRange = 0.05f..0.3f,
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Opacity",
                    color = Color.White
                )

                Slider(
                    value = currentOpacity,
                    onValueChange = { value ->
                        if (isAllSelected) {
                            onUpdateAll(null, value, null)
                        } else {
                            element.let {
                                when (it) {
                                    is ButtonElement -> onUpdate(it.copy(opacity = value))
                                    is AnalogStickElement -> onUpdate(it.copy(opacity = value))
                                    is DPadElement -> onUpdate(it.copy(opacity = value))
                                }
                            }
                        }
                    },
                    valueRange = 0.1f..1f,
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Enabled",
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    MaterialSwitch(
                        checked = currentEnabled,
                        onCheckedChange = { enabled ->
                            if (isAllSelected) {
                                onUpdateAll(null, null, enabled)
                            } else {
                                element.let {
                                    when (it) {
                                        is ButtonElement -> onUpdate(it.copy(enabled = enabled))
                                        is AnalogStickElement -> onUpdate(it.copy(enabled = enabled))
                                        is DPadElement -> onUpdate(it.copy(enabled = enabled))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}