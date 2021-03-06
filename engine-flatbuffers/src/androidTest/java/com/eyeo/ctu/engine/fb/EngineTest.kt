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

package com.eyeo.ctu.engine.fb

import com.eyeo.ctu.engine.fb.ContentType.Companion.SubDocument
import com.google.flatbuffers.FlatBufferBuilder
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer

class EngineTest {

    private val engine = Engine()
    private val URL = "http://www.domain.com/someResource.html"

    private inline fun createRequest(): FlatBufferBuilder {
        val builder = FlatBufferBuilder(0, DirectFlatBufferBuilder())
        val url = builder.createString(URL)
        val contentTypes = MatchesRequest.createContentTypesVector(
            builder, byteArrayOf(SubDocument))
        val documentUrl1 = builder.createString("http://www.domain.com/frame1.html")
        val documentUrl2 = builder.createString("http://www.domain.com/frame2.html")
        val documentUrl3 = builder.createString("http://www.domain.com/frame3.html")
        val documentUrls = MatchesRequest.createDocumentUrlsVector(
            builder, intArrayOf(documentUrl1, documentUrl2, documentUrl3)
        )

        MatchesRequest.startMatchesRequest(builder)
        MatchesRequest.addUrl(builder, url)
        MatchesRequest.addContentTypes(builder, contentTypes)
        MatchesRequest.addDocumentUrls(builder, documentUrls)
        MatchesRequest.addSpecificOnly(builder, true)
        val request = MatchesRequest.endMatchesRequest(builder)
        builder.finish(request)

        return builder
    }

    @Test
    fun testSerializeDeserialize() {
        val requestByteBuilder = createRequest()
        val requestBuffer = ByteBuffer.wrap(requestByteBuilder.sizedByteArray())
        val request = MatchesRequest.getRootAsMatchesRequest(requestBuffer)

        assertEquals(URL, request.url)
        assertEquals(1, request.contentTypesLength)
        assertEquals(SubDocument, request.contentTypes(0))

        assertEquals(3, request.documentUrlsLength)
        assertEquals("http://www.domain.com/frame1.html", request.documentUrls(0))
        assertEquals("http://www.domain.com/frame2.html", request.documentUrls(1))
        assertEquals("http://www.domain.com/frame3.html", request.documentUrls(2))

        assertTrue(request.specificOnly)
    }

    @Test
    fun testBuffer() {
        val requestBuilder = createRequest()
        val requestBuffer = requestBuilder.dataBuffer()
        val offset = requestBuffer.position()
        val responseByteArray = engine.matchesByteBuffer(requestBuffer, offset)
        assertNotNull(responseByteArray)
        val responseBuffer = ByteBuffer.wrap(responseByteArray)
        val response = MatchesResponse.Companion.getRootAsMatchesResponse(responseBuffer)
        assertEquals(URL.length.toULong(), response.filter?.pointer)
    }

    @Test
    fun testByteArray() {
        val requestBuilder = createRequest()
        val requestBytes = requestBuilder.sizedByteArray()
        val responseByteArray = engine.matchesByteArray(requestBytes)
        assertNotNull(responseByteArray)
        val responseBuffer = ByteBuffer.wrap(responseByteArray)
        val response = MatchesResponse.getRootAsMatchesResponse(responseBuffer)
        assertEquals(URL.length.toULong(), response.filter?.pointer)
    }
}