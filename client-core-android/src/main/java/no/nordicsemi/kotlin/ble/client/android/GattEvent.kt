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

@file:Suppress("unused")

package no.nordicsemi.kotlin.ble.client.android

import no.nordicsemi.kotlin.ble.client.RemoteService
import no.nordicsemi.kotlin.ble.core.ConnectionParameters
import no.nordicsemi.kotlin.ble.core.ConnectionState
import no.nordicsemi.kotlin.ble.core.Phy
import no.nordicsemi.kotlin.ble.core.PhyInUse
import no.nordicsemi.kotlin.ble.core.PhyOption

/**
 * A base class for all GATT events available with Android API.
 */
sealed class GattEvent {

    internal val isDisconnectionEvent: Boolean
        get() = this is ConnectionStateChanged && disconnected
}

/**
 * Event indicating that the connection state has changed.
 *
 * @param newState The new connection state.
 */
data class ConnectionStateChanged(val newState: ConnectionState) : GattEvent() {

    /** Returns whether the new state is [ConnectionState.Disconnected]. */
    val disconnected: Boolean
        get() = newState is ConnectionState.Disconnected
}

/**
 * Event indicating that the services have changed.
 *
 * @param services The list of discovered remote services.
 */
data class ServicesChanged(val services: List<RemoteService>) : GattEvent()

/**
 * Event indicating that the (Maximum Transfer Unit) MTU has changed.
 *
 * MTU is the maximum number of bytes that can be sent in a single Attribute Layer packet.
 * GATT protocol may use some of the bytes for its own headers, so the maximum size of the
 * payload is smaller, depending on the operation type.
 *
 * An Attribute Layer payload may be automatically split into multiple Link Layer packets.
 * The size of the Link Layer packet is 27 bytes by default, but it may be increased
 * using Data Length Extension feature, supported on Android 6+. The size of the
 * Link Layer packet is called the LL MTU and is not available using Android API.
 *
 * @param mtu The new MTU.
 */
data class MtuChanged(val mtu: Int) : GattEvent()

/**
 * Event indicating that the RSSI value has been read.
 *
 * The RSSI value is the signal strength of the signal received from the peripheral, in dBm.
 * The higher value, the stronger signal. Usually, RSSI is in range -100 dBm (far away) to
 * -30 dBm (very close). The value depends on multiple factors, such as distance, obstacles,
 * antenna, phone orientation, etc.
 *
 * @param rssi The RSSI value.
 */
data class RssiRead(val rssi: Int) : GattEvent()

/**
 * Event indicating that the PHY used for the connection has changed.
 *
 * PHY defines the Physical Layer properties. LE 1M is the legacy PHY, with a speed of 1 Mbps.
 * Some Android 8+ devices supports LE 2M, with a speed of 2 Mbps and LL Coded, with a coding
 * 2 or 8 bits per symbol, providing a longer range.
 *
 * @param phy The new PHY.
 * @see Phy
 * @see PhyOption
 */
data class PhyChanged(val phy: PhyInUse) : GattEvent()

/**
 * Event indicating that the connection parameters have changed.
 *
 * @param newParameters The new connection parameters.
 */
data class ConnectionParametersChanged(val newParameters: ConnectionParameters) : GattEvent()