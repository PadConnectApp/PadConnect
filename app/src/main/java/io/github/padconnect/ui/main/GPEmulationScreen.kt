/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */


package io.github.padconnect.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.padconnect.dialogs.AlertDialogQueue
import io.github.padconnect.dialogs.AppDialog
import io.github.padconnect.dialogs.EditPanel
import io.github.padconnect.ui.main.elements.AnalogStick
import io.github.padconnect.ui.main.elements.DPad
import io.github.padconnect.ui.main.elements.GamepadButton
import io.github.padconnect.utils.AnalogStickElement
import io.github.padconnect.utils.ButtonElement
import io.github.padconnect.utils.ControllerLayout
import io.github.padconnect.utils.DPadElement
import io.github.padconnect.utils.LayoutStorage
import io.github.padconnect.utils.LayoutStorage.updateElement
import io.github.padconnect.utils.settings.GlobalConfig
import io.github.padconnect.viewmodel.GPEmulationViewModel

@SuppressLint("UnusedBoxWithConstraintsScope", "SourceLockedOrientationActivity")
@Composable
fun GPEmulationScreen(
    layout: ControllerLayout,
    viewModel: GPEmulationViewModel,
    isEditMode: Boolean = false
) {
    val context = LocalContext.current

    val orientationMode by GlobalConfig.orientationModeFlow.collectAsState(0)

    var eLayout by remember {
        mutableStateOf(layout)
    }

    val defaultLayout = remember { LayoutStorage.createDefault(layout.name) }

    val controlPointers = remember { mutableSetOf<PointerId>() }
    val buttonBounds = remember { mutableStateMapOf<ButtonElement, Rect>() }
    val activeButtonPointers = remember { mutableStateMapOf<PointerId, ButtonElement>() }

    var selectedElementId by remember {
        mutableStateOf<String?>(null)
    }

    val selectedElement = remember(selectedElementId, eLayout) {
        eLayout.elements.firstOrNull {
            it.id == selectedElementId
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, isEditMode, eLayout) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && isEditMode) {
                val layouts = LayoutStorage.load(context)

                val index = layouts.indexOfFirst {
                    it.name == eLayout.name
                }

                if (index != -1) {
                    layouts[index] = eLayout
                    LayoutStorage.save(context, layouts)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(orientationMode) {
        val activity = context as? Activity ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation

        when (orientationMode) {
            0 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            1 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            2 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            3 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            4 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            5 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            6 -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }

        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    val showLatencyIndicator by GlobalConfig.showLatencyFlow.collectAsState(true)

    FullScreen()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(isEditMode) {
                if (isEditMode) {
                    detectTapGestures(
                        onTap = {
                            selectedElementId = null
                        }
                    )
                }
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    var cameraPointer: PointerId? = null
                    var lastPos = Offset.Zero
                    var currentVelocityX = 0f
                    var currentVelocityY = 0f
                    val sensitivity = 0.02f

                    while (true) {
                        if (isEditMode) return@awaitPointerEventScope
                        val event = awaitPointerEvent()

                        event.changes.forEach { change ->
                            if (change.changedToDown()) {
                                val hit = buttonBounds.entries
                                    .firstOrNull { it.value.contains(change.position) }
                                    ?.key

                                if (hit != null) {
                                    activeButtonPointers[change.id] = hit
                                    viewModel.transport?.setButton(hit.key.id, true)
                                    controlPointers.add(change.id)
                                    return@forEach
                                }
                            }

                            if (change.pressed && activeButtonPointers.containsKey(change.id)) {
                                val oldButton = activeButtonPointers[change.id]

                                val hit = buttonBounds.entries
                                    .firstOrNull { it.value.contains(change.position) }
                                    ?.key

                                if (hit != oldButton) {
                                    hit?.let { hit ->
                                        oldButton?.let {
                                            viewModel.transport?.setButton(it.key.id, false)
                                        }
                                        if (hit.enabled) viewModel.transport?.setButton(
                                            hit.key.id,
                                            true
                                        )
                                    }

                                    if (hit != null) {
                                        activeButtonPointers[change.id] = hit
                                    }
                                }

                                return@forEach
                            }

                            if (change.changedToUp()) {
                                if (activeButtonPointers.containsKey(change.id)) {
                                    activeButtonPointers[change.id]?.let {
                                        viewModel.transport?.setButton(it.key.id, false)
                                    }
                                    activeButtonPointers.remove(change.id)
                                    controlPointers.remove(change.id)
                                    return@forEach
                                }
                            }

                            if (cameraPointer == null && change.pressed && !controlPointers.contains(
                                    change.id
                                )
                            ) {
                                cameraPointer = change.id
                                lastPos = change.position
                            }

                            if (change.id == cameraPointer && change.pressed) {
                                val delta = change.position - lastPos
                                lastPos = change.position

                                currentVelocityX += delta.x * sensitivity
                                currentVelocityY -= delta.y * sensitivity

                                currentVelocityX = currentVelocityX.coerceIn(-1f, 1f)
                                currentVelocityY = currentVelocityY.coerceIn(-1f, 1f)

                                viewModel.transport?.setRightAxis(
                                    currentVelocityX,
                                    currentVelocityY
                                )
                            }

                            if (change.id == cameraPointer && !change.pressed) {
                                cameraPointer = null
                                currentVelocityX = 0f
                                currentVelocityY = 0f
                                viewModel.transport?.setRightAxis(0f, 0f)
                            }
                        }
                    }
                }
            }
    ) {
        if (showLatencyIndicator) LatencyIndicator(
            viewModel,
            modifier = Modifier.align(Alignment.TopStart)
        )
        eLayout.elements.forEach { element ->
            when (element) {
                is ButtonElement -> GamepadButton(
                    modifier = Modifier,
                    button = element,
                    screenWidth = maxWidth,
                    screenHeight = maxHeight,
                    buttonBounds = buttonBounds,
                    isPressed = activeButtonPointers.containsValue(element),
                    isEditMode = isEditMode,
                    isSelected = selectedElementId == element.id,
                    onSelect = {
                        selectedElementId = element.id
                    },
                    onUpdate = { updated ->
                        eLayout = eLayout.updateElement(updated.id) {
                            updated
                        }
                    }
                )

                is AnalogStickElement -> AnalogStick(
                    dpad = element,
                    transport = viewModel.transport,
                    screenWidth = maxWidth,
                    screenHeight = maxHeight,
                    controlPointers = controlPointers,
                    isEditMode = isEditMode,
                    isSelected = selectedElementId == element.id,
                    onSelect = {
                        selectedElementId = element.id
                    },
                    onUpdate = { updated ->
                        eLayout = eLayout.updateElement(updated.id) {
                            updated
                        }
                    }
                )

                is DPadElement -> DPad(
                    dpad = element,
                    transport = viewModel.transport,
                    screenWidth = maxWidth,
                    screenHeight = maxHeight,
                    controlPointers = controlPointers,
                    isEditMode = isEditMode,
                    isSelected = selectedElementId == element.id,
                    onSelect = { selectedElementId = element.id },
                    onUpdate = { updated ->
                        eLayout = eLayout.updateElement(updated.id) { updated }
                    }
                )
            }
        }

        if (isEditMode) {
            EditPanel(
                element = selectedElement,
                allElements = eLayout.elements,
                onUpdate = { updated ->
                    eLayout = eLayout.updateElement(updated.id) {
                        updated
                    }
                },
                onUpdateAll = { size, opacity, enabled ->
                    var tempLayout = eLayout
                    eLayout.elements.forEach { el ->
                        val updated = when (el) {
                            is ButtonElement -> el.copy(
                                size = size ?: el.size,
                                opacity = opacity ?: el.opacity,
                                enabled = enabled ?: el.enabled
                            )

                            is AnalogStickElement -> el.copy(
                                size = size ?: el.size,
                                opacity = opacity ?: el.opacity,
                                enabled = enabled ?: el.enabled
                            )

                            is DPadElement -> el.copy(
                                size = size ?: el.size,
                                opacity = opacity ?: el.opacity,
                                enabled = enabled ?: el.enabled
                            )
                        }
                        tempLayout = tempLayout.updateElement(updated.id) { updated }
                    }
                    eLayout = tempLayout
                },
                onReset = {
                    AlertDialogQueue.show(
                        AppDialog.Message(
                            title = "Reset Button: ${selectedElement?.id ?: "All"}",
                            message = "Are you sure you want to reset this button?",
                            onConfirm = {
                                if (selectedElementId != null) {
                                    val defaultElement =
                                        defaultLayout.elements.firstOrNull { it.id == selectedElementId }
                                    if (defaultElement != null) {
                                        eLayout =
                                            eLayout.updateElement(selectedElementId!!) { defaultElement }
                                    }
                                } else {
                                    eLayout = defaultLayout
                                }
                            }
                        )
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
fun FullScreen() {
    val context = LocalContext.current
    val window = (context as Activity).window
    val controller = remember {
        WindowCompat.getInsetsController(window, window.decorView)
    }

    DisposableEffect(Unit) {
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
private fun LatencyIndicator(viewModel: GPEmulationViewModel, modifier: Modifier = Modifier) {
    val lastLatency by viewModel.lastLatency.collectAsState()
    Text(
        text = if (lastLatency != null) String.format("%.1f ms", lastLatency) else "",
        modifier = modifier.padding(start = 25.dp),
        color = Color.White
    )
}
