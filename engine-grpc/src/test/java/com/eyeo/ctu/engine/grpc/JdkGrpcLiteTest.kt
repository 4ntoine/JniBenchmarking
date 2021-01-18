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

package com.eyeo.ctu.engine.grpc

import com.eyeo.ctu.engine.grpc.lite.BlockingFilter
import com.eyeo.ctu.engine.grpc.lite.EngineServiceGrpc
import com.eyeo.ctu.engine.grpc.lite.MatchesRequest
import com.eyeo.ctu.engine.grpc.lite.MatchesResponse
import com.eyeo.ctu.engine.grpc.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.grpc.wire.EngineServiceClient
import com.squareup.wire.GrpcClient
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress
import io.grpc.stub.StreamObserver
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class JdkGrpcLiteTest {
    companion object {
        const val PORT = 7777
        const val URL = "http://www.domain.com"
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

    private val service = EngineServiceImpl()
    private lateinit var server: Server

    @Before
    fun setUp() {
        server = NettyServerBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forPort(PORT)
            .addService(service)
            .build()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdownNow()
    }

    private fun test(channel: ManagedChannel) {
        val service = EngineServiceGrpc.newBlockingStub(channel)
        val request = MatchesRequest
            .newBuilder()
            .setUrl(URL)
            .build()
        val response = service.matches(request)
        assertNotNull(response)
        assertEquals(URL.length.toLong(), response.filter.pointer) // just to check the server logic
    }

    @Test
    fun testSendRequestAndReceiveResponse_tcp_lite() {
        val channel = NettyChannelBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forAddress("localhost", PORT)
            .usePlaintext()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_tcp_wire() {
        val grpcClient = GrpcClient.Builder()
            .client(
                OkHttpClient.Builder()
                .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
                .build())
            .baseUrl("http://localhost:$PORT")
            .build()

        val client = grpcClient.create(EngineServiceClient::class)
        val request = WireMatchesRequest(url = URL)
        val response = client.matches().executeBlocking(request)
        assertNotNull(response)
        assertEquals(URL.length.toLong(), response.filter!!.pointer)
    }
}