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

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import org.junit.Rule
import org.junit.Test
//import java.util.concurrent.CountDownLatch

class JniBenchmark {
    private val library = Library()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // no arguments

    @Test
    fun benchmarkStaticNoArgsNoResult() = benchmarkRule.measureRepeated {
        Library.nativeStaticNoArgsNoResult()
    }

    @Test
    fun benchmarkNoArgsNoResult() = benchmarkRule.measureRepeated {
        library.nativeNoArgsNoResult()
    }

    @Test
    fun benchmarkThreadSafeNoArgsNoResult_sameThread() = benchmarkRule.measureRepeated {
        library.nativeThreadSafeNoArgsNoResult()
    }

    /*
    @Test
    fun benchmarkThreadSafeNoArgsNoResult_bgThread() {
        class BackgroundThread : Thread() {
            private val threadStartedLatch = CountDownLatch(1)
            private val mayInvokeLatch = CountDownLatch(1)
            private val invokedLatch = CountDownLatch(1)

            override fun run() {
                threadStartedLatch.countDown() // signal started
                mayInvokeLatch.await() // waiting the signal to invoke

                library.nativeThreadSafeNoArgsNoResult()

                invokedLatch.countDown() // signal invoked
            }

            fun waitForStarted() = threadStartedLatch.await()
            fun mayInvoke() = mayInvokeLatch.countDown()
            fun waitForInvoked() = invokedLatch.await()
        }

        benchmarkRule.measureRepeated {
            val thread = runWithTimingDisabled {
                val thread = BackgroundThread()
                thread.waitForStarted()
                thread
            }
            thread.mayInvoke()
            thread.waitForInvoked()
        }
    }
    */

    @Test
    fun benchmarkNoArgsIntResult() = benchmarkRule.measureRepeated {
        library.nativeNoArgsIntResult()
    }

    @Test
    fun benchmarkNoArgsFloatResult() = benchmarkRule.measureRepeated {
        library.nativeNoArgsFloatResult()
    }

    @Test
    fun benchmarkNoArgsDoubleResult() = benchmarkRule.measureRepeated {
        library.nativeNoArgsDoubleResult()
    }

    @Test
    fun benchmarkNoArgsStringResult() = benchmarkRule.measureRepeated {
        library.nativeNoArgsStringResult()
    }

    // arguments: 1

    @Test
    fun benchmark1IntArgNoResult() = benchmarkRule.measureRepeated {
        library.nativeIntArgNoResult(1)
    }

    @Test
    fun benchmark1FloatArgNoResult() = benchmarkRule.measureRepeated {
        library.nativeFloatArgNoResult(1.0f)
    }

    @Test
    fun benchmark1DoubleArgNoResult() = benchmarkRule.measureRepeated {
        library.nativeDoubleArgNoResult(1.0)
    }

    @Test
    fun benchmark1StringArgNoResult() = benchmarkRule.measureRepeated {
        library.nativeStringArgNoResult("hello world")
    }

    // arguments: 2

    @Test
    fun benchmark2IntArgNoResult() = benchmarkRule.measureRepeated {
        library.native2IntArgNoResult(1, 2)
    }

    @Test
    fun benchmark2FloatArgNoResult() = benchmarkRule.measureRepeated {
        library.native2FloatArgNoResult(1.0f, 2.0f)
    }

    @Test
    fun benchmark2DoubleArgNoResult() = benchmarkRule.measureRepeated {
        library.native2DoubleArgNoResult(1.0, 2.0)
    }

    @Test
    fun benchmark2StringArgNoResult() = benchmarkRule.measureRepeated {
        library.native2StringArgNoResult("hello", "world")
    }

    // echo (returns type is equal to argument)

    @Test
    fun benchmarkIntEcho() = benchmarkRule.measureRepeated {
        library.nativeIntEcho(1)
    }

    @Test
    fun benchmarkFloatEcho() = benchmarkRule.measureRepeated {
        library.nativeFloatEcho(1.0f)
    }

    @Test
    fun benchmarkDoubleEcho() = benchmarkRule.measureRepeated {
        library.nativeDoubleEcho(1.0)
    }

    @Test
    fun benchmarkStringEcho() = benchmarkRule.measureRepeated {
        library.nativeStringEcho("hello world")
    }
}