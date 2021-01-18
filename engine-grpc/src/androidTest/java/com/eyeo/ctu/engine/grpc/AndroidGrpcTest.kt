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

import androidx.test.platform.app.InstrumentationRegistry
import com.eyeo.ctu.engine.grpc.lite.BlockingFilter
import com.eyeo.ctu.engine.grpc.lite.EngineServiceGrpc
import com.eyeo.ctu.engine.grpc.lite.MatchesRequest
import com.eyeo.ctu.engine.grpc.lite.MatchesResponse
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress
import io.grpc.okhttp.OkHttpChannelBuilder
import io.grpc.stub.StreamObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidRpcTest {
    companion object {
        const val JAVA_PORT = 7777
        const val JAVA_INPROCESS_CHANNEL_NAME = "ABP"
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
                .setFilter(
                    BlockingFilter
                        .newBuilder()
                        .setPointer(request.url.length.toLong()) // server logic: just for the test
                        .build()
                )
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    private val engineService = EngineServiceImpl()
    private lateinit var javaTcpSocketServer: Server
    private lateinit var javaInProcessServer: Server
    private lateinit var cppTcpSocketServer: Rpc
    private lateinit var cppUnixDomainSocketServer: Rpc
    private lateinit var unixSocketPath: String

    @Before
    fun setUp() {
        setUpCpp()
        setUpJava()
    }

    private fun setUpCpp() {
        setUpCppTcpSocketServer()
        setUpCppUnixDomainSocketServer()
    }

    private fun setUpCppUnixDomainSocketServer() {
        // the app (test) must have read/write permissions to the path, thus using cache directory
        val context = InstrumentationRegistry.getInstrumentation().context
        unixSocketPath = File(context.cacheDir, "abp").absolutePath

        cppUnixDomainSocketServer = Rpc.forUnixDomainSocket(unixSocketPath)
        cppUnixDomainSocketServer.start()
    }

    private fun setUpCppTcpSocketServer() {
        cppTcpSocketServer = Rpc.forTcpPort(CPP_PORT)
        cppTcpSocketServer.start()
    }

    private fun setUpJava() {
        setUpTcpSocketServer()
        setUpInProcessServer()
    }

    private fun setUpInProcessServer() {
        javaInProcessServer = InProcessServerBuilder
            .forName(JAVA_INPROCESS_CHANNEL_NAME)
            .addService(engineService)
            .directExecutor()
            .build()
        javaInProcessServer.start()
    }

    private fun setUpTcpSocketServer() {
        javaTcpSocketServer = NettyServerBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forPort(JAVA_PORT)
            .addService(engineService)
            .directExecutor()
            .build()
        javaTcpSocketServer.start()
    }

    @After
    fun tearDown() {
        javaTcpSocketServer.shutdownNow()
        javaInProcessServer.shutdownNow()
        cppTcpSocketServer.shutdownNow()
        cppUnixDomainSocketServer.shutdownNow()
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
    fun testSendRequestAndReceiveResponse_tcp_lite() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", JAVA_PORT)
            .usePlaintext()
            .directExecutor()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_inProcess_lite() {
        val channel = InProcessChannelBuilder
            .forName(JAVA_INPROCESS_CHANNEL_NAME)
            .usePlaintext()
            .directExecutor()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_unixDomainSocket_lite_epoll() {
        val channel = NettyChannelBuilder
            .forAddress(DomainSocketAddress(unixSocketPath))
            .eventLoopGroup(EpollEventLoopGroup())
            .channelType(EpollDomainSocketChannel::class.java)
            .usePlaintext()
            .directExecutor()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_unixDomainSocket_lite_java() {
        // `forAddress()` arguments does not matter as it's not used further
        // (just have to pass validation), see below.
        // `socketFactory()` available on `OkHttpChannelBuilder` only
        val channel = OkHttpChannelBuilder
            .forAddress("localhost", JAVA_PORT) // no matter what
            .socketFactory(JavaUnixDomainSocketFactory(unixSocketPath)) // custom transport
            .usePlaintext()
            .directExecutor()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_socket_lite_cpp() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", CPP_PORT)
            .usePlaintext()
            .build()
        test(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_socket_wire_java() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", JAVA_PORT)
            .usePlaintext()
            .build()
        test(channel)
    }
}
