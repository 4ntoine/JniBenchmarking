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

package com.eyeo.ctu.engine.grpc

class Rpc private constructor(private val address: String) {
    companion object {
        init {
            System.loadLibrary("engine-grpc")
        }

        fun forTcpPort(port: Int) = Rpc("0.0.0.0:$port")
        fun forUnixDomainSocket(path: String) = Rpc("unix://$path")
    }

    // raw pointer to grpc::Server returned by native side
    private var pointer: Long? = null
    fun start() {
        pointer = start(address)
    }
    private external fun start(address: String): Long

    fun shutdownNow() {
        pointer?.let {
            shutdownNow(it)
            pointer = null
        }
    }
    private external fun shutdownNow(pointer: Long)
}

