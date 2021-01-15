package com.eyeo.ctu.jni

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import org.junit.Rule
import org.junit.Test

abstract class ClassA        { abstract fun someTestMethod()    }
open class ClassB : ClassA() { override fun someTestMethod(){}; fun someBaseMethod(){} }
open class ClassC : ClassB() { override fun someTestMethod(){}; fun someTestMethod(someArg: Int){} }

class PureJniBenchmark {

    private val library = Measurement()
    private val libraryClassName = library::class.java.canonicalName!!.replace(".", "/")
    private val cClassName = ClassC::class.java.canonicalName!!.replace(".", "/")

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // no arguments

    @Test
    fun benchmarkStaticNoArgsNoResult() = benchmarkRule.measureRepeated {
        Measurement.nativeStaticNoArgsNoResult()
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

    @Test
    fun benchmarkNoArgsNoResultAllocateString() = benchmarkRule.measureRepeated {
        library.nativeNoArgsNoResultAllocateString()
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

    @Test
    fun benchmarkFindClass() = benchmarkRule.measureRepeated {
        library.nativeFindClass(libraryClassName)
    }

    @Test
    fun benchmarkFindMethod() = benchmarkRule.measureRepeated {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        library.nativeFindMethod(methodName, methodSignature)
    }

    @Test
    fun benchmarkFindClassAndMethod_declared() = benchmarkRule.measureRepeated {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        library.nativeFindClassAndMethod(libraryClassName, methodName, methodSignature)
    }

    @Test
    fun benchmarkFindMethod_overridenSuperClass() = benchmarkRule.measureRepeated {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        library.nativeFindClassAndMethod(cClassName, methodName, methodSignature)
    }

    @Test
    fun benchmarkFindMethod_overridenSameClass() = benchmarkRule.measureRepeated {
        // it has another signature method with the same name but different args
        val methodName = "someBaseMethod"
        val methodSignature = "()V"
        library.nativeFindClassAndMethod(cClassName, methodName, methodSignature)
    }

    @Test
    fun benchmarkFindMethod_superClass() = benchmarkRule.measureRepeated {
        val methodName = "someBaseMethod"
        val methodSignature = "()V"
        library.nativeFindClassAndMethod(cClassName, methodName, methodSignature)
    }

    @Test
    fun benchmarkCallJavaFromNative_findAndCall() = benchmarkRule.measureRepeated {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        library.nativeCallJavaFromNative(library, methodName, methodSignature)
    }

    @Test
    fun benchmarkCallJavaFromNative_overriden() = benchmarkRule.measureRepeated {
        val methodName = "someTestMethod"
        val methodSignature = "()V"
        val c = ClassC()
        library.nativeCallJavaFromNative(c, methodName, methodSignature)
    }

    @Test
    fun benchmarkCallJavaFromNative_callAsConcrete() = benchmarkRule.measureRepeated {
        // method is already found, it's just pure costs of the call
        library.nativeCallJavaFromNativeAsConcrete()
    }

    @Test
    fun benchmarkCallJavaFromNative_callAsInterface() = benchmarkRule.measureRepeated {
        library.nativeCallJavaFromNativeAsInterface()
    }

    @Test
    fun benchmarkCallJavaFromNative_callAsAbstract() = benchmarkRule.measureRepeated {
        library.nativeCallJavaFromNativeAsAbstract()
    }
}