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

import com.sun.jna.Function
import com.sun.jna.FunctionMapper
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import java.lang.reflect.Method

// Generates the function name according to JNI rules (eg. Java_classname_methodname)
// (for some reason the default behavior is to map Function names 1:1,
// so one have to either call native methods equals to Java class method name,
// or have Java class methods names like 'Java_classname_methodname').
class JniFunctionMapper : FunctionMapper {
    override fun getFunctionName(library: NativeLibrary, method: Method): String =
        "Java_${method.declaringClass.canonicalName!!.replace(".", "_")}_${method.name}"
}

interface JnaLibrary : com.sun.jna.Library {
    companion object {
        private val options = mutableMapOf(
            // default options
            com.sun.jna.Library.OPTION_CALLING_CONVENTION to Function.C_CONVENTION,
            com.sun.jna.Library.OPTION_CLASSLOADER to JnaLibrary::class.java.classLoader,

            // [weird] trick to fix methods mapping
            com.sun.jna.Library.OPTION_FUNCTION_MAPPER to JniFunctionMapper(),
        )
        val INSTANCE = Native.load("benchmark", JnaLibrary::class.java, options)
    }

    fun nativeNoArgsNoResult()
    fun native2IntArgNoResult(arg1: Int, arg2: Int)
}