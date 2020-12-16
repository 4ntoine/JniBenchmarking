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

package com.eyeo.ctu.rpc

import com.eyeo.ctu.engine.protobuf.rpc.BlockingFilter
import com.eyeo.ctu.engine.protobuf.rpc.EngineServiceGrpc
import com.eyeo.ctu.engine.protobuf.rpc.MatchesRequest
import com.eyeo.ctu.engine.protobuf.rpc.MatchesResponse
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AndroidRpcTest {
    companion object {
        const val PORT = 7777
    }

    // service impl
    class EngineServiceImpl : EngineServiceGrpc.EngineServiceImplBase() {
        override fun matches(
            request: MatchesRequest,
            responseObserver: StreamObserver<MatchesResponse>
        ) {
            val response = MatchesResponse
                .newBuilder()
                .setFilter(BlockingFilter
                    .newBuilder()
                    .setPointer(request.url.length.toLong()) // server logic: just for the test
                    .build())
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    private lateinit var server: Server

    @Before
    fun setUp() {
        server = NettyServerBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forPort(PORT)
            .addService(EngineServiceImpl())
            .build()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdownNow()
    }

    @Test
    fun testSendRequestAndReceiveResponse_lite() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", PORT)
            .usePlaintext()
            .build()
        val service = EngineServiceGrpc.newBlockingStub(channel)

        val url = "http://www.domain.com"
        val request = MatchesRequest
            .newBuilder()
            .setUrl(url)
            .build()
        val response = service.matches(request)
        assertNotNull(response)
        assertEquals(url.length.toLong(), response.filter.pointer) // just to check the server logic
    }

    @Test
    fun testSendRequestAndReceiveResponse_wire() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", PORT)
            .usePlaintext()
            .build()
        val service = EngineServiceGrpc.newBlockingStub(channel)

        val url = "http://www.domain.com"
        val request = MatchesRequest
            .newBuilder()
            .setUrl(url)
            .build()
        val response = service.matches(request)
        assertNotNull(response)
        assertEquals(url.length.toLong(), response.filter.pointer) // just to check the server logic
    }
}