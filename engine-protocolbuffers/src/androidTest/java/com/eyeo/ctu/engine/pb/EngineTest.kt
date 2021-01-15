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

package com.eyeo.ctu.engine.pb

import com.eyeo.ctu.engine.pb.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.pb.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.pb.lite.ContentType as LiteContentType
import com.eyeo.ctu.engine.pb.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.pb.wire.MatchesResponse as WireMatchesResponse
import com.eyeo.ctu.engine.pb.wire.ContentType as WireContentType
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer

class EngineTest {

    private val engine = Engine()
    private val URL = "http://www.domain.com/someResource.html"

    @Test
    fun testMatches_array_lite() {
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
        val responseByteArray = engine.matchesByteArray(requestByteArray)
        assertNotNull(responseByteArray)
        val response = LiteMatchesResponse.parseFrom(responseByteArray)
        assertEquals(URL.length.toLong(), response.filter.pointer)
    }

    @Test
    fun testMatches_array_wire() {
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
        val responseByteArray = engine.matchesByteArray(requestByteArray)
        assertNotNull(responseByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
        assertEquals(URL.length.toLong(), response.filter?.pointer)
    }

    @Test
    fun testMatches_buffer_wire() {
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
        val responseByteArray = engine.matchesByteBuffer(requestByteBuffer)
        assertNotNull(responseByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
        assertEquals(URL.length.toLong(), response.filter?.pointer)
    }

    @Test
    fun testMatches_buffer_wire_withOutputStream() {
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

        val requestByteBuffer = ByteBuffer.allocateDirect(150)
        // 150 is actual size measured with this params
        request.encode(DirectByteBufferOutputStream(requestByteBuffer))
        val responseByteArray = engine.matchesByteBuffer(requestByteBuffer)
        assertNotNull(responseByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
        assertEquals(URL.length.toLong(), response.filter?.pointer)
    }
}