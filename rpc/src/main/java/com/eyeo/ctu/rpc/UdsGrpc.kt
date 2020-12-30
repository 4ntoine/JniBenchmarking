/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-present eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.eyeo.ctu.rpc

import android.net.LocalSocket
import android.net.LocalSocketAddress
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import javax.net.SocketFactory

/**
 * `java.net.Socket` adapter for `android.net.LocalSocket`.
 * Warning: this is not full impl, but rather minimal - just good enough to pass the gRPC test.
 */
class UnixDomainSocket(path: String): Socket() {
    private var socket = LocalSocket(LocalSocket.SOCKET_STREAM)
    private val socketAddress = LocalSocketAddress(path, LocalSocketAddress.Namespace.FILESYSTEM)

    init {
        connect()
    }

    private fun connect() = socket.connect(socketAddress)
    override fun connect(endpoint: SocketAddress?) = connect()
    override fun connect(endpoint: SocketAddress?, timeout: Int) = socket.connect(socketAddress, timeout)
    override fun bind(bindpoint: SocketAddress?) = socket.bind(socketAddress)
    override fun isConnected() =  socket.isConnected
    override fun isClosed() = socket.isClosed
    override fun isBound() = socket.isBound
    override fun shutdownInput() = socket.shutdownInput()
    override fun shutdownOutput() = socket.shutdownOutput()
    override fun isInputShutdown() = socket.isInputShutdown
    override fun isOutputShutdown() = socket.isOutputShutdown
    override fun close() = socket.close()
    override fun getInputStream() = socket.inputStream
    override fun getOutputStream() = socket.outputStream

    override fun setTcpNoDelay(on: Boolean) {
        // nothing
        // (otherwise UnsupportedException is thrown)
    }
}

/**
 * `javax.net.SocketFactory` impl to connect over Unix Domain Sockets
 * (uses UnixDomainSocket)
 */
class JavaUnixDomainSocketFactory(
    private val path: String
) : SocketFactory() {
    override fun createSocket(
        host: String,
        port: Int
    ) = UnixDomainSocket(path)

    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ) = UnixDomainSocket(path)

    override fun createSocket(
        host: InetAddress,
        port: Int
    ) = UnixDomainSocket(path)

    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ) = UnixDomainSocket(path)
}
