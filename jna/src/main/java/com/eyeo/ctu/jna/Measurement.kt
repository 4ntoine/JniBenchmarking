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

package com.eyeo.ctu.jna

import com.sun.jna.Native

interface Measurement : com.sun.jna.Library {
    companion object {
        val INSTANCE = Native.load("measurement-jna", Measurement::class.java)
    }

    // no arguments

    fun jnaNativeNoArgsNoResult()
    fun jnaNativeNoArgsIntResult(): Int
    fun jnaNativeNoArgsFloatResult(): Float
    fun jnaNativeNoArgsDoubleResult(): Double

    // 1 argument

    fun jnaNativeIntArgNoResult(arg: Int)
    fun jnaNativeFloatArgNoResult(arg: Float)
    fun jnaNativeDoubleArgNoResult(arg: Double)
    fun jnaNativeStringArgNoResult(arg: String)

    // 2 arguments

    fun jnaNative2IntArgNoResult(arg1: Int, arg2: Int)
    fun jnaNative2FloatArgNoResult(arg1: Float, arg2: Float)
    fun jnaNative2DoubleArgNoResult(arg1: Double, arg2: Double)
    fun jnaNative2StringArgNoResult(arg1: String, arg2: String)

    // echo

    fun jnaNativeIntEcho(arg: Int): Int
    fun jnaNativeFloatEcho(arg: Float): Float
    fun jnaNativeDoubleEcho(arg: Double): Double
    fun jnaNativeStringEcho(arg: String): String
}