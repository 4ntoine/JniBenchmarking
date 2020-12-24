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

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.concurrent.thread

abstract class ClassA        { abstract fun someTestMethod()    }
open class ClassB : ClassA() { override fun someTestMethod() {} }
open class ClassC : ClassB() { override fun someTestMethod() {} }

class LibraryTest {

    private val library = Library()
    private val libraryClassName = library::class.java.canonicalName!!.replace(".", "/")

    @Test
    fun testCallFromBackgroundThread() {
        library.nativeThreadSafeNoArgsNoResult()
        val t = thread {
            library.nativeThreadSafeNoArgsNoResult()
        }
        t.start()
        t.join()
    }

    @Test
    fun testFindClass() {
        assertTrue(library.nativeFindClass(libraryClassName))
    }

    @Test
    fun testFindClassAndMethod() {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        assertTrue(library.nativeFindClassAndMethod(libraryClassName, methodName, methodSignature))
    }

    @Test
    fun testFindMethod() {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        assertTrue(library.nativeFindMethod(methodName, methodSignature))
    }

    @Test
    fun testFindOverridenMethod() {
        val cClassName = ClassC::class.java.canonicalName!!.replace(".", "/")
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        assertTrue(library.nativeFindClassAndMethod(cClassName, methodName, methodSignature))
    }

    @Test
    fun testCallJavaFromNative() {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        var called = false
        val overridenLibrary = object : Library() {
            override fun someTestMethod() {
                called = true
            }
        }
        assertTrue(library.nativeCallJavaFromNative(overridenLibrary, methodName, methodSignature))
        assertTrue(called)
    }

    @Test
    fun testCallJavaFromNative_concrete() {
        var called = false
        val overridenLibrary = object : Library() {
            override fun concreteMethod() {
                called = true
            }
        }

        overridenLibrary.nativeCallJavaFromNativeAsConcrete()
        assertTrue(called)
    }

    @Test
    fun testCallJavaFromNative_iface() {
        var called = false
        val overridenLibrary = object : Library() {
            override fun ifaceMethod() {
                called = true
            }
        }

        overridenLibrary.nativeCallJavaFromNativeAsInterface()
        assertTrue(called)
    }

    @Test
    fun testCallJavaFromNative_abstract() {
        var called = false
        val overridenLibrary = object : Library() {
            override fun abstractMethod() {
                called = true
            }
        }

        overridenLibrary.nativeCallJavaFromNativeAsAbstract()
        assertTrue(called)
    }
}