JNI overhead benchmarking
=

## Background

ABP current architecture optimizes for reusability meaning libadblockplus-android
is a Java/Android wrapper for libadblockplus and libadblockplus is a C++ wrapper
for adblockpluscore which is written in JavaScript.
Libadblockplus-android contains a JNI glue code that bridges managed and unmanaged code
and uses libadblockplus C++ SDK behind the scenes. It has some costs due to un-/marshalling
of resources and it's important to know it.

## Core Technology Unit context

The goal of CTU (Platform team) is to work out the optimal technical solution for ad blocking SDK
that works for the stakeholders. DPU is one of CTU major stakeholders and its transitive stakeholders
are interested in best possible performance as one of architecture-significant requirements.
Knowing the costs of managed/unmanaged code interaction and how it affects the performance overall
is important when architecting. For instance, it could provide a better understanding of pros and cons of
NDK-based solutions (eg. C++ or Rust implementations) vs. multiplatform solutions (eg. Kotlin Multiplatform Mobile).

## Current JNI code highlights

* Current JNI code in libadblockplus-android is optimized for performance:
  * class/ctors searches are done during `JNI_OnLoad` where possible
  * `DirectByteBuffer` is used to pass large strings
* considered to be thread-safe: `JniEnvAcquire` class provide automatic threads attachment/detachments where needed
* uses smart pointers for libadblockplus entities where possible
* some API is asynchronous (eg. HTTP client and FileSystem)

## Useful links

* [JNI performance benchmark](https://web.archive.org/web/20120210162557/http://janet-project.sourceforge.net/papers/jnibench.pdf)
* [Android JNI tips](https://developer.android.com/training/articles/perf-jni)
* [Best practices for using the Java Native Interface](https://developer.ibm.com/articles/j-jni/)
* [Protobuf alternative to JNI](http://boyw165.github.io/android/2016/10/15/jni-and-protobuf.html)
* [Djinni library](https://github.com/dropbox/djinni)