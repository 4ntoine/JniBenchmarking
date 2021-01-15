package com.eyeo.ctu.engine.pb

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import org.junit.Rule
import org.junit.Test
import java.nio.ByteBuffer
import com.eyeo.ctu.engine.pb.lite.MatchesRequest as LiteMatchesRequest
import com.eyeo.ctu.engine.pb.lite.MatchesResponse as LiteMatchesResponse
import com.eyeo.ctu.engine.pb.lite.ContentType as LiteContentType
import com.eyeo.ctu.engine.pb.wire.MatchesRequest as WireMatchesRequest
import com.eyeo.ctu.engine.pb.wire.MatchesResponse as WireMatchesResponse
import com.eyeo.ctu.engine.pb.wire.ContentType as WireContentType

class EngineBenchmark {

    private val engine = Engine()

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun testMatches_lite_array() = benchmarkRule.measureRepeated {
        // serialization by "protobuf lite"
        val request = LiteMatchesRequest.newBuilder()
            .setUrl("http://www.domain.com/someResource.html")
            .addContentTypes(LiteContentType.SubDocument)
                .addDocumentUrls("http://www.domain.com/frame1.html")
                .addDocumentUrls("http://www.domain.com/frame2.html")
                .addDocumentUrls("http://www.domain.com/frame3.html")
            .setSpecificOnly(true)
            .build()
        val requestByteArray = request.toByteArray()
        val responseByteArray = engine.matchesByteArray(requestByteArray)
        val response = LiteMatchesResponse.parseFrom(responseByteArray)
    }

    @Test
    fun testMatches_wire_array() = benchmarkRule.measureRepeated {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = "http://www.domain.com/someResource.html",
            contentTypes = listOf(
                WireContentType.SubDocument
            ),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"
            ),
            specificOnly = true)
        val requestByteArray = request.encode()
        val responseByteArray = engine.matchesByteArray(requestByteArray)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
    }

    @Test
    fun testMatches_wire_buffer() = benchmarkRule.measureRepeated {
        // serialization by "square wire"
        val request = WireMatchesRequest(
            url = "http://www.domain.com/someResource.html",
            contentTypes = listOf(
                WireContentType.SubDocument
            ),
            documentUrls = listOf(
                "http://www.domain.com/frame1.html",
                "http://www.domain.com/frame2.html",
                "http://www.domain.com/frame3.html"
            ),
            specificOnly = true)

        val requestByteBuffer = ByteBuffer.allocateDirect(150)
        // 150 is actual size measured with this params
        request.encode(DirectByteBufferOutputStream(requestByteBuffer))
        val responseByteArray = engine.matchesByteBuffer(requestByteBuffer)
        val response = WireMatchesResponse.ADAPTER.decode(responseByteArray!!)
    }
}