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

import com.eyeo.ctu.engine.protobuf.MatchesRequest
import com.eyeo.ctu.engine.protobuf.MatchesResponse
import com.eyeo.ctu.engine.protobuf.ContentType as ProtobufContentType
import org.junit.Assert.*
import org.junit.Test

class EngineTest {

    private val engine = Engine()

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
    fun testProtoMatches_lite() {
        // serialization by "protobuf lite"
        val url = "http://www.domain.com/someResource.html"
        val request = MatchesRequest.newBuilder()
            .setUrl(url)
            .addContentTypes(ProtobufContentType.Subdocument)
                .addDocumentUrls("http://www.domain.com/frame1.html")
                .addDocumentUrls("http://www.domain.com/frame2.html")
                .addDocumentUrls("http://www.domain.com/frame3.html")
            .setSpecificOnly(true)
            .build()
        val requestByteArray = request.toByteArray()
        val responseByteArray = engine.protoMatchesByteArray(requestByteArray)
        val response = MatchesResponse.parseFrom(responseByteArray)
        assertEquals(url.length, response.filter.pointer)
    }

//    @Test
//    fun testProtoMatches_t() {
//        // serialization by "wire:3.5.0"
//        val request = MatchesRequest(
//            url = "http://www.domain.com/someResource.html",
//            contentTypes = MatchesRequest.ContentType.Subdocument,
//            documentUrls = listOf(
//                "http://www.domain.com/frame1.html",
//                "http://www.domain.com/frame2.html",
//                "http://www.domain.com/frame3.html"),
//            specificOnly = true)
//        val byteArray = request.encode()
//        val response = engine.protoMatchesByteArray(byteArray)
//    }
}