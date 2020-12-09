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

class CalculatorBenchmark {
    companion object {
        const val TIMES = 10_000
    }

    private val calculator = Calculator()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmarkCalculatorAdd_overJni() = benchmarkRule.measureRepeated {
        // multiple (TIMES) JNI calls
        repeat(TIMES) {
            calculator.add(1.0f, 2.0f)
        }
    }

    @Test
    fun benchmarkCalculatorAdd_native() = benchmarkRule.measureRepeated {
        // it's single JNI call that is doing TIMES calculation in native code
        calculator.timesAdd(TIMES, 1.0f, 2.0f)
    }
}