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

package com.eyeo.ctu

class Library {
    companion object {
        init {
            System.loadLibrary("benchmark")
        }

        @JvmStatic
        external fun nativeStaticNoArgsNoResult()
    }

    // no arguments

    external fun nativeNoArgsNoResult()
    external fun nativeThreadSafeNoArgsNoResult()
    external fun nativeNoArgsIntResult(): Int
    external fun nativeNoArgsFloatResult(): Float
    external fun nativeNoArgsDoubleResult(): Double
    external fun nativeNoArgsStringResult(): String

    // 1 argument

    external fun nativeIntArgNoResult(arg: Int)
    external fun nativeFloatArgNoResult(arg: Float)
    external fun nativeDoubleArgNoResult(arg: Double)
    external fun nativeStringArgNoResult(arg: String)

    // 2 arguments

    external fun native2IntArgNoResult(arg1: Int, arg2: Int)
    external fun native2FloatArgNoResult(arg1: Float, arg2: Float)
    external fun native2DoubleArgNoResult(arg1: Double, arg2: Double)
    external fun native2StringArgNoResult(arg1: String, arg2: String)
}