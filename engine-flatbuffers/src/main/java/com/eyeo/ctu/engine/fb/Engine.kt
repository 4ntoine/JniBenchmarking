package com.eyeo.ctu.engine.fb

import java.nio.ByteBuffer

class Engine {
    companion object {
        init {
            System.loadLibrary("engine-fb")
        }
    }

    external fun matchesByteArray(requestBytes: ByteArray): ByteArray?
    external fun matchesByteBuffer(requestBuffer: ByteBuffer, offset: Int): ByteArray?
}