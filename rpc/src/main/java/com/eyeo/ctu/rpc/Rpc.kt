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

class Rpc private constructor(private val address: String) {
    companion object {
        init {
            System.loadLibrary("rpc")
        }

        fun forTcpPort(port: Int) = Rpc("0.0.0.0:$port")
        fun forUnixDomainSocket(path: String) = Rpc("unit://$path")
    }

    fun start() = start(address)
    private external fun start(address: String)
    external fun shutdownNow()
}

