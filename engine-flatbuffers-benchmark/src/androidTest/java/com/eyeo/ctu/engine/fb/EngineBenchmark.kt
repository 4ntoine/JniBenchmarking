package com.eyeo.ctu.engine.fb

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import com.google.flatbuffers.FlatBufferBuilder
import org.junit.Rule
import org.junit.Test
import java.nio.ByteBuffer

class EngineBenchmark {

    private val engine = Engine()
    private val URL = "http://www.domain.com/someResource.html"

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // "inline" to improve performance
    private inline fun createRequest(): FlatBufferBuilder {
        val builder = FlatBufferBuilder(0, DirectFlatBufferBuilder())
        val url = builder.createString(URL)
        val contentTypes = MatchesRequest.createContentTypesVector(
            builder, byteArrayOf(ContentType.SubDocument)
        )
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

    // Flatbuffers have no de-/serialization costs, but do have "data filling",
    // so it has to be included into benchmarking.

    @Test
    fun testPassingByteArray() = benchmarkRule.measureRepeated {
        val requestBuilder = createRequest()
        val requestBuffer = requestBuilder.dataBuffer()
        val offset = requestBuffer.position()
        val responseByteArray = engine.matchesByteBuffer(requestBuffer, offset)
        val responseBuffer = ByteBuffer.wrap(responseByteArray)
        val response = MatchesResponse.getRootAsMatchesResponse(responseBuffer)
    }

    @Test
    fun testPassingDirectBuffer() = benchmarkRule.measureRepeated {
        val requestBuilder = createRequest()
        val requestBuffer = requestBuilder.dataBuffer()
        val offset = requestBuffer.position()
        val responseByteArray = engine.matchesByteBuffer(requestBuffer, offset)
        val responseBuffer = ByteBuffer.wrap(responseByteArray)
        val response = MatchesResponse.getRootAsMatchesResponse(responseBuffer)
    }

    @Test
    fun testPassingWithoutFillingBenchmarked() {
        val requestBuilder = createRequest()
        val requestBuffer = requestBuilder.dataBuffer()
        val offset = requestBuffer.position()

        benchmarkRule.measureRepeated {
            val responseByteArray = engine.matchesByteBuffer(requestBuffer, offset)
            val responseBuffer = ByteBuffer.wrap(responseByteArray)
            val response = MatchesResponse.getRootAsMatchesResponse(responseBuffer)
        }
    }
}