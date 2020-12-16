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
import com.eyeo.ctu.engine.protobuf.rpc.EngineServiceGrpc
import com.eyeo.ctu.engine.protobuf.rpc.MatchesRequest
import com.eyeo.ctu.engine.protobuf.rpc.MatchesResponse
import io.grpc.ManagedChannelBuilder
import com.eyeo.ctu.engine.protobuf.rpc.BlockingFilter as RpcBlockingFilter
import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver
import org.junit.Rule
import org.junit.Test
import org.junit.After
import org.junit.Before

class RpcBenchmark {
    companion object {
        const val PORT = 7777
    }

    // service impl
    class EngineServiceImpl : EngineServiceGrpc.EngineServiceImplBase() {
        override fun matches(
            request: MatchesRequest,
            responseObserver: StreamObserver<MatchesResponse>
        ) {
            // no server-side logic (just to measure the RPC performance)
            val response = MatchesResponse
                .newBuilder()
                .setFilter(RpcBlockingFilter
                    .newBuilder()
                    .build())
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    private lateinit var server: Server

    @get:Rule
    val benchmarkRule = BenchmarkRule()

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
    fun testSendRequestAndReceiveResponse_pureRpc() {
        // assume channel is created once and reused across the calls,
        // so it's excluded from measured part
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

        benchmarkRule.measureRepeated {
            val response = service.matches(request)
        }
    }
}