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
import com.eyeo.ctu.engine.protobuf.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.protobuf.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.protobuf.lite.ContentType as LiteContentType
import com.eyeo.ctu.engine.protobuf.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.protobuf.wire.MatchesResponse as WireMatchesResponse
import com.eyeo.ctu.engine.protobuf.wire.ContentType as WireContentType
import org.junit.Rule
import org.junit.Test
import java.nio.ByteBuffer

class EngineBenchmark {

    private val engine = Engine()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun testMatches_jni() = benchmarkRule.measureRepeated {
        engine.matches(
            "http://www.domain.com/someResource.html",
            setOf(ContentType.SubDocument),
            listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"),
            null,
            true)
    }

//    @Test
//    fun testGetListedSubscriptions() = benchmarkRule.measureRepeated {
//        engine.getListedSubscriptions()
//    }

    @Test
    fun testProtoMatches_array_lite() = benchmarkRule.measureRepeated {
        // serialization by "protobuf lite"
        val request = LiteMatchesRequest.newBuilder()
            .setUrl("http://www.domain.com/someResource.html")
            .addContentTypes(LiteContentType.SubDocument)
                .addDocumentUrls("http://www.domain.com/frame1.html")
                .addDocumentUrls("http://www.domain.com/frame2.html")
                .addDocumentUrls("http://www.domain.com/frame3.html")
            .setSpecificOnly(true)
            .build()
        val requestByteArray = request.toByteArray()
        val responseByteArray = engine.protoMatchesByteArray(requestByteArray)
        val response = LiteMatchesResponse.parseFrom(responseByteArray)
    }

    @Test
    fun testProtoMatches_array_wire() = benchmarkRule.measureRepeated {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = "http://www.domain.com/someResource.html",
            contentTypes = listOf(
                WireContentType.SubDocument
            ),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"
            ),
            specificOnly = true)
        val requestByteArray = request.encode()
        val responseByteArray = engine.protoMatchesByteArray(requestByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
    }

    @Test
    fun testProtoMatches_buffer_wire() = benchmarkRule.measureRepeated {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = "http://www.domain.com/someResource.html",
            contentTypes = listOf(
                WireContentType.SubDocument
            ),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"
            ),
            specificOnly = true)

        val requestByteBuffer = ByteBuffer.allocateDirect(150)
        // 150 is actual size measured with this params
        request.encode(DirectByteBufferOutputStream(requestByteBuffer))
        val responseByteArray = engine.protoMatchesByteBuffer(requestByteBuffer)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
    }
}