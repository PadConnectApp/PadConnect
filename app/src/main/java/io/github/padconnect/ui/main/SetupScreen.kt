/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */


package io.github.padconnect.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.padconnect.R
import io.github.padconnect.utils.settings.GlobalConfig
import io.github.padconnect.viewmodel.GPEmulationViewModel

data class SetupStep(
    val title: String,
    val subtitle: String,
    val buttonText: String? = null,
    val buttonAction: (() -> Unit)? = null,
    val url: String? = null
)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(viewModel: GPEmulationViewModel? = null, navigateTo: ((String) -> Unit)? = null) {
    val context = LocalContext.current
    val isTransportConnected by viewModel!!.isTransportConnected.collectAsState(false)

    val steps = listOf(
        SetupStep(
            title = "Setup required on your PC",
            subtitle = "Download PadConnectReceiver on your PC",
            buttonText = "Download Receiver",
            url = "https://github.com/PadConnectApp/PadConnectReceiver/releases/tag/v1.0.0"
        ),
        SetupStep(
            title = "Install Driver",
            subtitle = "Install ViGEm driver (required only for windows users)",
            buttonText = "Download ViGEm",
            url = "https://github.com/nefarius/ViGEmBus/releases/latest"
        ),
        SetupStep(
            title = "Run Receiver",
            subtitle = "Open PadConnectReceiver on your PC",
            buttonText = null,
            url = null
        ),
        SetupStep(
            title = "Connect Devices",
            subtitle = "Make sure both devices are on the same WiFi network",
            buttonText = "Done",
            buttonAction = {
                navigateTo?.invoke("HOME")
                GlobalConfig.INITIAL_SETUP_FINISHED.boolean = true
            },
            url = null
        )
    )

    var currentStep by remember { mutableIntStateOf(0) }

    val step = steps[currentStep]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = step.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                step.buttonText?.let {
                    Button(
                        enabled = if (it == "Done") isTransportConnected else true,
                        onClick = {
                            step.buttonAction?.let { action ->
                                action.invoke()
                                return@Button
                            }
                            step.url?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Text(
                            if (it == "Done") {
                                if (isTransportConnected) it else "Connecting"
                            } else it
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            if (currentStep > 0) currentStep--
                        },
                        enabled = currentStep > 0
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_keyboard_arrow_left), contentDescription = "Previous")
                    }

                    Text(
                        text = "${currentStep + 1} / ${steps.size}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    IconButton(
                        onClick = {
                            if (currentStep < steps.lastIndex) currentStep++
                        },
                        enabled = currentStep < steps.lastIndex
                    ) {
                        Icon(painter = painterResource(io.github.compose_preferences.R.drawable.ic_keyboard_arrow_right), contentDescription = "Next")
                    }
                }
            }
        }
    }
}