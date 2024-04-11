/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.kotlin.ble.android.sample.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.kotlin.ble.client.android.Peripheral
import no.nordicsemi.kotlin.ble.client.android.preview.PreviewPeripheral
import no.nordicsemi.kotlin.ble.core.ConnectionState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeviceList(
    devices: List<Peripheral>,
    onItemClick: (Peripheral) -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
    ) {
        if (devices.isNotEmpty()) {
            stickyHeader {
                Text(text = "Tap on a device to connect")
            }
        }

        items(devices) { device ->
            DeviceItem(
                device = device,
                onClick = { onItemClick(device) }
            )
        }
    }
}

@Composable
fun DeviceItem(
    device: Peripheral,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by device.state.collectAsStateWithLifecycle()
    val animatedColor by animateColorAsState(
        targetValue = when (state) {
            is ConnectionState.Connected -> Color.Green
            is ConnectionState.Connecting -> Color.Gray
            else -> contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface)
        },
        label = "background color animation"
    )
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = device.name ?: "Unknown device")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Icon",
            tint = animatedColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NordicTheme {
        val scope = rememberCoroutineScope()
        DeviceList(
            modifier = Modifier.fillMaxWidth(),
            devices = listOf(
                PreviewPeripheral(scope, "AA:BB:CC:DD:EE:FF", "Mock device 1", state = ConnectionState.Connected),
                PreviewPeripheral(scope, "00:11:22:33:44:55", "Mock device 2", state = ConnectionState.Connecting),
                PreviewPeripheral(scope, "AA:BB:CC:DD:EE:00", "Mock device 3"),
            ),
            onItemClick = {},
            contentPadding = PaddingValues(16.dp),
        )
    }
}