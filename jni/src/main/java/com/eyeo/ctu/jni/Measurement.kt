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

package com.eyeo.ctu.jni

interface Iface {
    fun ifaceMethod()
}

abstract class Base {
    abstract fun abstractMethod()
}

open class Measurement : Iface, Base() {
    companion object {
        init {
            System.loadLibrary("measurement-jni")
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
    external fun nativeNoArgsNoResultAllocateString()

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

    // echo

    external fun nativeIntEcho(arg: Int): Int
    external fun nativeFloatEcho(arg: Float): Float
    external fun nativeDoubleEcho(arg: Double): Double
    external fun nativeStringEcho(arg: String): String

    // find

    // do not remove! it's needed for search from JNI
    open fun someTestMethod() {}

    external fun nativeFindClass(mangledJavaClassName: String): Boolean
    external fun nativeFindClassAndMethod(mangledJavaClassName: String,
                                          methodName: String,
                                          methodSignature: String): Boolean

    external fun nativeFindMethod(methodName: String,
                                  methodSignature: String): Boolean

    // call

    // do not remove! it's needed for search from JNI
    // concrete class method
    open fun concreteMethod() {}

    // interface method
    override fun ifaceMethod() {}

    // base class method impl
    override fun abstractMethod() {}

    external fun nativeCallJavaFromNative(obj: Any,
                                          methodName: String,
                                          methodSignature: String): Boolean

    external fun nativeCallJavaFromNativeAsConcrete()  // concrete class method
    external fun nativeCallJavaFromNativeAsInterface() // interface method impl
    external fun nativeCallJavaFromNativeAsAbstract()  // abstract method impl
}