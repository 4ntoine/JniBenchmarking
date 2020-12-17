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

import com.eyeo.ctu.engine.protobuf.rpc.lite.BlockingFilter
import com.eyeo.ctu.engine.protobuf.rpc.lite.EngineServiceGrpc
import com.eyeo.ctu.engine.protobuf.rpc.lite.MatchesRequest
import com.eyeo.ctu.engine.protobuf.rpc.lite.MatchesResponse
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AndroidRpcTest {
    companion object {
        const val JAVA_PORT = 7777
        const val JAVA_INPROCESS_CHANNEL = "ABP"
        const val CPP_PORT = 7778
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

    private val engineService = EngineServiceImpl()
    private lateinit var javaSocketServer: Server
    private lateinit var javaInProcessServer: Server
    private lateinit var cppServer: Rpc

    @Before
    fun setUp() {
        setUpJava()
        setUpCpp()
    }

    private fun setUpCpp() {
        cppServer = Rpc(CPP_PORT)
        cppServer.start()
    }

    private fun setUpJava() {
        setUpSocketServer()
        setUpInProcessServer()
    }

    private fun setUpInProcessServer() {
        javaInProcessServer = InProcessServerBuilder
            .forName(JAVA_INPROCESS_CHANNEL)
            .addService(engineService)
            .build()
        javaInProcessServer.start()
    }

    private fun setUpSocketServer() {
        javaSocketServer = NettyServerBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forPort(JAVA_PORT)
            .addService(engineService)
            .build()
        javaSocketServer.start()
    }

    @After
    fun tearDown() {
        javaSocketServer.shutdownNow()
        javaInProcessServer.shutdownNow()
        cppServer.shutdownNow()
    }

    private fun test(channel: ManagedChannel) {
        val url = "http://www.domain.com"
        val request = MatchesRequest
            .newBuilder()
            .setUrl(url)
            .build()
        val service = EngineServiceGrpc.newBlockingStub(channel)
        val response = service.matches(request)
        assertNotNull(response)
        assertEquals(url.length.toLong(), response.filter.pointer) // just to check the server logic
    }

    @Test
    fun testSendRequestAndReceiveResponse_http_lite() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", JAVA_PORT)
            .usePlaintext()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_inProcess_lite() {
        val channel = InProcessChannelBuilder
            .forName(JAVA_INPROCESS_CHANNEL)
            .usePlaintext()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_lite_cpp() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", CPP_PORT)
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
            .forAddress("localhost", JAVA_PORT)
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