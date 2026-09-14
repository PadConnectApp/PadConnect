/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */

package io.github.padconnect.ui.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.compose_preferences.core.PreferenceSubtitle
import io.github.compose_preferences.preference.HomePreference
import io.github.compose_preferences.preference.RegularPreference
import io.github.compose_preferences.preference.SingleSelectionDialog
import io.github.compose_preferences.preference.SwitchPreference
import io.github.padconnect.R
import io.github.padconnect.utils.settings.GlobalConfig

data class IntOption(
    val value: Int,
    val label: String
)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateTo: ((String) -> Unit)? = null
) {
    Scaffold { contentPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(key = "advanced_settings") {
                HomePreference(
                    title = stringResource(R.string.advanced_settings),
                    icon = { Icon(painterResource(R.drawable.ic_tune), null) },
                    description = stringResource(R.string.advanced_settings_description),
                    onClick = {
                        navigateTo?.invoke("advanced_settings")
                    }
                )
            }

            item(key = "theme_settings") {
                HomePreference(
                    title = stringResource(R.string.theme_settings),
                    icon = { Icon(painterResource(R.drawable.ic_palette), null) },
                    description = stringResource(R.string.theme_settings_description),
                    onClick = {
                        navigateTo?.invoke("theme_settings")
                    }
                )
            }

            item(key = "about") {
                HomePreference(
                    title = stringResource(R.string.about),
                    icon = { Icon(painterResource(R.drawable.ic_info), null) },
                    description = stringResource(R.string.about_desc),
                    onClick = {
                        navigateTo?.invoke("about")
                    }
                )
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    modifier: Modifier = Modifier,
    navigateTo: ((String) -> Unit)? = null,
    navigateBack: (() -> Unit)? = null
) {
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            .then(modifier), topBar = {
            LargeTopAppBar(title = {
                    Text(
                        text = stringResource(R.string.advanced_settings)
                    )
            }, scrollBehavior = topBarScrollBehavior, navigationIcon = {
                if (navigateBack != null) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_left),
                            contentDescription = null
                        )
                    }
                }
            })
        }) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            item("core_settings") {
                RegularPreference(
                    title = stringResource(R.string.core_settings),
                    onClick = {
                        navigateTo?.invoke("core_settings")
                    }
                )
            }

            item("display_settings") {
                RegularPreference(
                    title = stringResource(R.string.display_settings),
                    onClick = {
                        navigateTo?.invoke("display_settings")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreSettingsScreen(
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null
) {
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            .then(modifier), topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(R.string.core_settings)
                )
            }, scrollBehavior = topBarScrollBehavior, navigationIcon = {
                if (navigateBack != null) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_left),
                            contentDescription = null
                        )
                    }
                }
            })
        }) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            item(key = "input_update_rate") {
                IntSetting(
                    title = "Input update rate",
                    summary = "How many times per second input is sent",
                    value = { GlobalConfig.INPUT_UPDATE_RATE.int },
                    onValueChange = { GlobalConfig.INPUT_UPDATE_RATE.int = it },
                    labelsId = R.array.input_update_rate_labels,
                    valuesId = R.array.input_update_rate_values
                )
            }

            item(key = "enable_rumble") {
                SwitchSetting(
                    title = "Enable Haptic Feedback",
                    summary = "Uses your phone's vibration to simulate controller rumble (may vary by device)",
                    value = { GlobalConfig.ENABLE_RUMBLE.boolean },
                    onValueChange = { GlobalConfig.ENABLE_RUMBLE.boolean = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySettingsScreen(
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null
) {
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            .then(modifier), topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(R.string.display_settings)
                )
            }, scrollBehavior = topBarScrollBehavior, navigationIcon = {
                if (navigateBack != null) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_left),
                            contentDescription = null
                        )
                    }
                }
            })
        }) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            item(key = "show_latency") {
                SwitchSetting(
                    title = "Show Latency",
                    value = { GlobalConfig.SHOW_LATENCY.boolean },
                    onValueChange = { GlobalConfig.SHOW_LATENCY.boolean = it }
                )
            }

            item(key = "orientation_mode") {
                IntSetting(
                    title = "Orientation",
                    value = { GlobalConfig.ORIENTATION_MODE.int },
                    onValueChange = { GlobalConfig.ORIENTATION_MODE.int = it },
                    labelsId = R.array.orientation_mode_labels,
                    valuesId = R.array.orientation_mode_values
                )
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null
) {
    val topBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            .then(modifier), topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(R.string.theme_settings)
                )
            }, scrollBehavior = topBarScrollBehavior, navigationIcon = {
                if (navigateBack != null) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painterResource(id = R.drawable.ic_keyboard_arrow_left),
                            contentDescription = null
                        )
                    }
                }
            })
        }) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            item(key = "theme_mode") {
                IntSetting(
                    title = "Theme mode",
                    value = { GlobalConfig.THEME_MODE.int },
                    onValueChange = { GlobalConfig.THEME_MODE.int = it },
                    labelsId = R.array.theme_mode_labels,
                    valuesId = R.array.theme_mode_values
                )
            }
        }
    }
}

@Composable
fun IntSetting(
    title: String,
    summary: String? = null,
    value: () -> Int,
    onValueChange: (Int) -> Unit,
    labelsId: Int,
    valuesId: Int
) {
    val context = LocalContext.current

    val labels = context.resources.getStringArray(labelsId)
    val values = context.resources.getIntArray(valuesId)

    val options = remember {
        values.mapIndexed { index, value ->
            IntOption(
                value = value,
                label = labels.getOrElse(index) { value.toString() }
            )
        }
    }

    var currentValue by rememberSaveable { mutableIntStateOf(value()) }

    SingleSelectionDialog(
        currentValue = options.first { it.value == currentValue },
        values = options,
        icon = null,
        title = title,
        onValueChange = { option: IntOption ->
            currentValue = option.value
            onValueChange(option.value)
        },
        subtitle = summary?.let { { PreferenceSubtitle(it) } },
        valueToText = { option -> option.label }
    )
}

@Composable
fun SwitchSetting(
    title: String,
    summary: String? = null,
    value: () -> Boolean,
    onValueChange: (Boolean) -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(value()) }

    SwitchPreference(
        checked = checked,
        title = title,
        onClick = {
            checked = !checked
            onValueChange(checked)
        }
    )
}
