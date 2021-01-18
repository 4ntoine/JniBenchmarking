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

#include <string>
#include <vector>
#include <matches.pb.h>
#include <Engine.h>

using namespace com::eyeo::ctu::engine::pb;

#define JNI_VERSION JNI_VERSION_1_6

void JniEngine_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved)
{

}

void JniEngine_OnUnload(JavaVM* vm, JNIEnv* env, void* reserved)
{

}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return JNI_ERR;
    }

    JniEngine_OnLoad(vm, env, reserved);

    return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return;
    }

    JniEngine_OnUnload(vm, env, reserved);
}

// ---

Engine engine;

static void matches(jbyte* requestBuffer, jsize requestBufferSize, jbyte* responseBuffer)
{
    // 1. deserialize request
    MatchesRequest request;
    request.ParseFromArray(requestBuffer, requestBufferSize);

    std::vector<std::string> documentsUrls;
    documentsUrls.reserve(request.documenturls_size());
    for (int i = 0; i < request.documenturls_size(); i++)
    {
        documentsUrls.push_back(request.documenturls(i));
    }

    // 2. process
    auto filter = engine.matches(
            request.url(),
            1, /* for simplicity */
            documentsUrls,
            request.sitekey(),
            request.specificonly());

    // 3. serialize response
    MatchesResponse response;
    response.mutable_filter()->set_pointer((uint64_t)filter->pointer());

    int size = response.ByteSizeLong();
    response.SerializeToArray(responseBuffer, size);
}

// warning: use protobuf descriptors or update if `MatchesResponse` is changed
#define RESPONSE_SIZE 4

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_eyeo_ctu_engine_pb_Engine_matchesByteArray(
        JNIEnv *env, jobject thiz, jbyteArray jRequestByteArray)
{
    jbyte* requestBuffer = env->GetByteArrayElements(jRequestByteArray, NULL);
    jsize requestBufferSize = env->GetArrayLength(jRequestByteArray);
    int size = RESPONSE_SIZE;
    jbyte* temp = new jbyte[size];

    matches(requestBuffer, requestBufferSize, temp);

    jbyteArray jResponseByteArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jResponseByteArray, 0, size, temp);
    delete[] temp;

    return jResponseByteArray;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_eyeo_ctu_engine_pb_Engine_matchesByteBuffer(
        JNIEnv *env, jobject thiz, jobject jRequestByteBuffer)
{
    jbyte* requestBuffer = static_cast<jbyte*>(env->GetDirectBufferAddress(jRequestByteBuffer));
    jsize requestBufferSize = env->GetDirectBufferCapacity(jRequestByteBuffer);
    int size = RESPONSE_SIZE;
    jbyte* temp = new jbyte[size];

    matches(requestBuffer, requestBufferSize, temp);

    jbyteArray jResponseByteArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jResponseByteArray, 0, size, temp);
    delete[] temp;

    return jResponseByteArray;
}