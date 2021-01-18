package com.eyeo.ctu.engine.grpc

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.platform.app.InstrumentationRegistry
import com.squareup.wire.GrpcClient
import io.grpc.*
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress
import io.grpc.okhttp.OkHttpChannelBuilder
import io.grpc.stub.StreamObserver
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

import com.eyeo.ctu.engine.grpc.lite.EngineServiceGrpc
import com.eyeo.ctu.engine.grpc.wire.EngineServiceClient
import com.eyeo.ctu.engine.grpc.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.grpc.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.grpc.lite.BlockingFilter as LiteBlockingFilter

import com.eyeo.ctu.engine.grpc.wire.MatchesRequest as WireMatchesRequest

class EngineBenchmark {

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
                .setFilter(LiteBlockingFilter
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
    private lateinit var cppUnixDomainSocketServer: Rpc
    private lateinit var unixSocketPath: String

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Before
    fun setUp() {
        setUpSocketServer()
        setUpInProcessServer()
        setUpCppUnixDomainSocketServer()
    }

    private fun setUpInProcessServer() {
        javaInProcessServer = InProcessServerBuilder
            .forName(JAVA_INPROCESS_CHANNEL)
            .addService(engineService)
            .directExecutor() // that should be reportedly a huge performance win (but seems to be almost the same)
            .build()
        javaInProcessServer.start()
    }

    private fun setUpCppUnixDomainSocketServer() {
        // the app (test) must have read/write permissions to the path, thus using cache directory
        val context = InstrumentationRegistry.getInstrumentation().context
        unixSocketPath = File(context.cacheDir, "abp").absolutePath

        cppUnixDomainSocketServer = Rpc.forUnixDomainSocket(unixSocketPath)
        cppUnixDomainSocketServer.start()
    }

    private fun setUpSocketServer() {
        javaSocketServer = NettyServerBuilder // have to use Netty.. explicitly (instead of Managed..)
            .forPort(JAVA_PORT)
            .addService(engineService)
            .directExecutor() // that should be reportedly a huge performance win (but seems to be almost the same)
            .build()
        javaSocketServer.start()
    }

    @After
    fun tearDown() {
        javaSocketServer.shutdownNow()
        javaInProcessServer.shutdownNow()
        cppUnixDomainSocketServer.shutdownNow()
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
    fun testSendRequestAndReceiveResponse_lite_socket() {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", JAVA_PORT)
            .usePlaintext()
            .directExecutor() // that should be reportedly a huge performance win (but seems to be almost the same)
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_lite_inProcess() {
        val channel = InProcessChannelBuilder
            .forName(JAVA_INPROCESS_CHANNEL)
            .usePlaintext()
            .compressorRegistry(CompressorRegistry.newEmptyInstance())
            .decompressorRegistry(DecompressorRegistry.emptyInstance())
            .directExecutor() // that should be reportedly a huge performance win (but seems to be almost the same)
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_unixDomainSocket_epoll() {
        val channel = NettyChannelBuilder
            .forAddress(DomainSocketAddress(unixSocketPath))
            .eventLoopGroup(EpollEventLoopGroup())
            .channelType(EpollDomainSocketChannel::class.java)
            .usePlaintext()
            .directExecutor()
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_java_unixDomainSocket() {
        // `forAddress()` arguments does not matter as it's not used further
        // (just have to pass validation), see below.
        // `socketFactory()` available on `OkHttpChannelBuilder` only
        val channel = OkHttpChannelBuilder
            .forAddress("localhost", JAVA_PORT) // no matter what
            .socketFactory(JavaUnixDomainSocketFactory(unixSocketPath)) // custom transport
            .usePlaintext()
            .directExecutor()
            .build()
        measure(channel)
    }

    @Test
    fun testSendRequestAndReceiveResponse_wire_socket() {
        val grpcClient = GrpcClient.Builder()
            .client(OkHttpClient.Builder()
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