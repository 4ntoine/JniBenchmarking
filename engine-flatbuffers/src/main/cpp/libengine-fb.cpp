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

#include <jni.h>
#include <string>
#include <vector>
#include <matches_request_generated.h>
#include <matches_response_generated.h>
#include <Engine.h>

using namespace com::eyeo::ctu::engine::fb;

#define JNI_VERSION JNI_VERSION_1_6

// precached in JNI_OnLoad and released in JNI_OnUnload
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

    return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return;
    }
}

// ---

Engine engine;

static jbyteArray matches(JNIEnv *env, void* requestBuffer, jsize requestBufferSize)
{
    // 1. deserialize request
    auto request = GetMatchesRequest(requestBuffer);

    std::vector<std::string> documentsUrls;
    documentsUrls.reserve(request->documentUrls()->size());
    for (int i = 0; i < request->documentUrls()->size(); i++)
    {
        documentsUrls.push_back(request->documentUrls()->Get(i)->str());
    }

    // 2. process
    auto filter = engine.matches(
            request->url()->str(),
            1, // for simplicity
            documentsUrls,
            request->sitekey() ? request->sitekey()->str() : "", // optional, so required `nullptr` checking
            request->specificOnly());

    // 3. serialize response
    flatbuffers::FlatBufferBuilder builder;
    auto response = CreateMatchesResponse(
            builder, new com::eyeo::ctu::engine::fb::BlockingFilter(filter->pointer()));
    builder.Finish(response);

    // 4. pass over JNI
    int responseBufferSize = builder.GetSize();
    jbyte* responseBuffer = reinterpret_cast<jbyte*>(builder.GetBufferPointer());
    jbyteArray jResponseByteArray = env->NewByteArray(responseBufferSize);
    env->SetByteArrayRegion(jResponseByteArray, 0, responseBufferSize, responseBuffer);

    return jResponseByteArray;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_eyeo_ctu_engine_fb_Engine_matchesByteArray(
        JNIEnv *env, jobject thiz, jbyteArray jRequestByteArray)
{
    void* requestBuffer = env->GetByteArrayElements(jRequestByteArray, NULL);
    jsize requestBufferSize = env->GetArrayLength(jRequestByteArray);
    return matches(env, requestBuffer, requestBufferSize);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_eyeo_ctu_engine_fb_Engine_matchesByteBuffer(
        JNIEnv *env, jobject thiz, jobject jRequestByteBuffer, jint jOffset)
{
    unsigned char* requestBuffer = reinterpret_cast<unsigned char*>(
            env->GetDirectBufferAddress(jRequestByteBuffer)) + jOffset;
    // have to skip offset - in flatbuffers it starts not from the beginning
    jsize requestBufferSize = env->GetDirectBufferCapacity(jRequestByteBuffer) - jOffset;
    return matches(env, requestBuffer, requestBufferSize);
}