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

import com.squareup.wire.ProtoReader
import com.eyeo.ctu.engine.protobuf.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.protobuf.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.protobuf.lite.ContentType as LiteContentType
import com.eyeo.ctu.engine.protobuf.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.protobuf.wire.MatchesResponse as WireMatchesResponse
import com.eyeo.ctu.engine.protobuf.wire.ContentType as WireContentType
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer

class EngineTest {

    private val engine = Engine()
    private val URL = "http://www.domain.com/someResource.html"

    @Test
    fun testMatches() {
        val filter = engine.matches(
            "http://www.domain.com/someResource.html",
            setOf(ContentType.SubDocument),
            listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"),
            null,
            true)
        assertNotNull(filter)
        val blockingFilter = filter as BlockingFilter
        assertNotNull(blockingFilter.pointer)
    }

//    @Test
//    fun testGetListedSubscriptions() {
//        val subscriptions = engine.getListedSubscriptions()
//        assertEquals(2, subscriptions.size)
//        assertTrue(subscriptions.first().startsWith("http://"))
//    }

    @Test
    fun testProtoMatches_array_lite() {
        // serialization by "protobuf lite"
        val request = LiteMatchesRequest.newBuilder()
            .setUrl(URL)
            .addContentTypes(LiteContentType.SubDocument)
                .addDocumentUrls("http://www.domain.com/frame1.html")
                .addDocumentUrls("http://www.domain.com/frame2.html")
                .addDocumentUrls("http://www.domain.com/frame3.html")
            .setSpecificOnly(true)
            .build()
        val requestByteArray = request.toByteArray()
        val responseByteArray = engine.protoMatchesByteArray(requestByteArray)
        assertNotNull(responseByteArray)
        val response = LiteMatchesResponse.parseFrom(responseByteArray)
        assertEquals(URL.length.toLong(), response.filter.pointer)
    }

    @Test
    fun testProtoMatches_array_wire() {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = URL,
            contentTypes = listOf(
                WireContentType.SubDocument),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"),
            specificOnly = true)
        val requestByteArray = request.encode()
        val responseByteArray = engine.protoMatchesByteArray(requestByteArray)
        assertNotNull(responseByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
        assertEquals(URL.length.toLong(), response.filter?.pointer)
    }

    @Test
    fun testProtoMatches_buffer_wire() {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = URL,
            contentTypes = listOf(
                WireContentType.SubDocument),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"),
            specificOnly = true)
        val requestByteArray = request.encode()
        val requestByteBuffer = ByteBuffer.allocateDirect(requestByteArray.size)
        requestByteBuffer.put(requestByteArray)
        val responseByteArray = engine.protoMatchesByteBuffer(requestByteBuffer)
        assertNotNull(responseByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
        assertEquals(URL.length.toLong(), response.filter?.pointer)
    }
}