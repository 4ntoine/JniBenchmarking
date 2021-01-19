package com.eyeo.ctu.engine.jvm

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import org.junit.Rule
import org.junit.Test

class PureJvmBenchmark {

    private val engine = Engine()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun testMatches() = benchmarkRule.measureRepeated {
        val filter = engine.matches(
            "http://www.domain.com/someResource.html",
            setOf(ContentType.SubDocument),
            listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"),
            null,
            true)
    }
}