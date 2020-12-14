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
import java.nio.ByteBuffer

enum class ContentType {
    Image,
    Script,
    SubDocument
    // ...
}

// filters
abstract class Filter
class BlockingFilter(val pointer: Long) : Filter()
class ExceptionFilter : Filter()

// subscription
class Subscription(url: String)

class Engine {
    companion object {
        init {
            System.loadLibrary("engine")
        }
    }

    // JNI

    external fun matches(url: String,
                         contentTypes: Set<ContentType>,
                         documentUrls: List<String>,
                         siteKey: String?,
                         specificOnly: Boolean): Filter?

    external fun getListedSubscriptions(): List<String>

    // protobuf

    external fun protoMatchesByteArray(requestBytes: ByteArray): ByteArray?

    external fun protoMatchesByteBuffer(requestBuffer: ByteBuffer): ByteBuffer?

}