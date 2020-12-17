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
import com.eyeo.ctu.engine.protobuf.rpc.lite.EngineServiceGrpc
import com.eyeo.ctu.engine.protobuf.rpc.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.protobuf.rpc.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.protobuf.rpc.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.protobuf.rpc.wire.EngineServiceClient
import com.squareup.wire.GrpcClient
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import com.eyeo.ctu.engine.protobuf.rpc.lite.BlockingFilter as RpcBlockingFilter
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.junit.Rule
import org.junit.Test
import org.junit.After
import org.junit.Before

class RpcBenchmark {
    companion object {
        const val JAVA_PORT = 7777
        const val JAVA_INPROCESS_CHANNEL = "ABP"
        const val URL = "http://www.domain.com"
    }

    // service impl
    class EngineServiceImpl : EngineServiceGrpc.EngineServiceImplBase() {
        override fun matches(
            request: LiteMatchesRequest,
            responseObserver: StreamObserver<LiteMatchesResponse>
        ) {
            // no server-side logic (just to measure the RPC performance)
            val response = LiteMatchesResponse
                .newBuilder()
                .setFilter(RpcBlockingFilter
                    .newBuilder()
                    .build())
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    private lateinit var javaSocketServer: Server
    private lateinit var javaInProcessServer: Server
    private val engineService = EngineServiceImpl()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Before
    fun setUp() {
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
    }

    private fun measure(channel: ManagedChannel) {
        // assume channel is created once and reused across the calls,
        // so it's excluded from measured part
        val service = EngineServiceGrpc.newBlockingStub(channel)
        val request = LiteMatchesRequest
            .newBuilder()
            .setUrl(URL)
            .build()

        benchmarkRule.measureRepeated {
            val response = service.matches(request)
        }
    }

    @Test
    fun testSendRequestAndReceiveResponse_pureRpc_socket_lite() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", JAVA_PORT)
            .usePlaintext()
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_pureRpc_inProcess_lite() {
        val channel = InProcessChannelBuilder
            .forName(JAVA_INPROCESS_CHANNEL)
            .usePlaintext()
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_pureRpc_wire() {
        val grpcClient = GrpcClient.Builder()
            .client(
                OkHttpClient.Builder()
                .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
                .build())
            .baseUrl("http://localhost:${JAVA_PORT}")
            .build()

        val client = grpcClient.create(EngineServiceClient::class)
        val request = WireMatchesRequest(url = URL)

        benchmarkRule.measureRepeated {
            val response = client.matches().executeBlocking(request)
        }
    }
}