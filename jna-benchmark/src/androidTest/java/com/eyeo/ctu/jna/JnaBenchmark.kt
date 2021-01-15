package com.eyeo.ctu.jna

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import org.junit.Rule
import org.junit.Test

class JnaBenchmark {

    private val library = Measurement.INSTANCE

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // no arguments

    @Test
    fun benchmarkNoArgsNoResult() = benchmarkRule.measureRepeated {
        library.jnaNativeNoArgsNoResult()
    }

    @Test
    fun benchmarkNoArgsIntResult() = benchmarkRule.measureRepeated {
        library.jnaNativeNoArgsIntResult()
    }

    @Test
    fun benchmarkNoArgsFloatResult() = benchmarkRule.measureRepeated {
        library.jnaNativeNoArgsFloatResult()
    }

    @Test
    fun benchmarkNoArgsDoubleResult() = benchmarkRule.measureRepeated {
        library.jnaNativeNoArgsDoubleResult()
    }

    // arguments: 1

    @Test
    fun benchmark1IntArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNativeIntArgNoResult(1)
    }

    @Test
    fun benchmark1FloatArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNativeFloatArgNoResult(1.0f)
    }

    @Test
    fun benchmark1DoubleArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNativeDoubleArgNoResult(1.0)
    }

    @Test
    fun benchmark1StringArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNativeStringArgNoResult("hello world")
    }

    // arguments: 2

    @Test
    fun benchmark2IntArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNative2IntArgNoResult(1, 2)
    }

    @Test
    fun benchmark2FloatArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNative2FloatArgNoResult(1.0f, 2.0f)
    }

    @Test
    fun benchmark2DoubleArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNative2DoubleArgNoResult(1.0, 2.0)
    }

    @Test
    fun benchmark2StringArgNoResult() = benchmarkRule.measureRepeated {
        library.jnaNative2StringArgNoResult("hello", "world")
    }

    // echo (returns type is equal to argument)

    @Test
    fun benchmarkIntEcho() = benchmarkRule.measureRepeated {
        library.jnaNativeIntEcho(1)
    }

    @Test
    fun benchmarkFloatEcho() = benchmarkRule.measureRepeated {
        library.jnaNativeFloatEcho(1.0f)
    }

    @Test
    fun benchmarkDoubleEcho() = benchmarkRule.measureRepeated {
        library.jnaNativeDoubleEcho(1.0)
    }

    @Test
    fun benchmarkStringEcho() = benchmarkRule.measureRepeated {
        library.jnaNativeStringEcho("hello world")
    }
}