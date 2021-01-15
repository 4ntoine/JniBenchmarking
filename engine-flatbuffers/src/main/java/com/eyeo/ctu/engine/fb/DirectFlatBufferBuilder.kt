package com.eyeo.ctu.engine.fb

import com.google.flatbuffers.FlatBufferBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Allocates direct memory
 * (to be passed to native side without copying)
 */
class DirectFlatBufferBuilder : FlatBufferBuilder.ByteBufferFactory() {
    override fun newByteBuffer(capacity: Int): ByteBuffer
            = ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN)
}