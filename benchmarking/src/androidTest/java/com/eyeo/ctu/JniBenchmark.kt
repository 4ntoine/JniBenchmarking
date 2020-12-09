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
}