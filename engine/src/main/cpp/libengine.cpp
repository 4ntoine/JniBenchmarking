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

#include "Utils.h"
#include "Engine.h"
#include <string>
#include <vector>
#include <matches.pb.h>

// precached in JNI_OnLoad and released in JNI_OnUnload
JniGlobalReference<jclass>* blockingFilterClass;
jmethodID blockingFilterCtor;
std::vector<std::string> listedSubscriptions;

void JniEngine_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved)
{
    blockingFilterClass = new JniGlobalReference<jclass>(env, env->FindClass("com/eyeo/ctu/BlockingFilter"));
    blockingFilterCtor = env->GetMethodID(blockingFilterClass->Get(), "<init>", "(J)V");
}

void JniEngine_OnUnload(JavaVM* vm, JNIEnv* env, void* reserved)
{
    if (blockingFilterClass)
    {
        delete blockingFilterClass;
        blockingFilterClass = nullptr;
    }
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return JNI_ERR;
    }

    JniUtils_OnLoad(vm, env, reserved);
    JniEngine_OnLoad(vm, env, reserved);

    listedSubscriptions.push_back("https://easylist-downloads.adblockplus.org/easylist.txt");
    listedSubscriptions.push_back("https://easylist-downloads.adblockplus.org/ruadlist+easylist.txt");

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
    JniUtils_OnUnload(vm, env, reserved);
}

// ---

Engine engine;

extern "C"
JNIEXPORT jobject JNICALL
Java_com_eyeo_ctu_Engine_matches(JNIEnv *env,
                                 jobject thiz,
                                 jstring jUrl,
                                 jobject jContentTypes,
                                 jobject jDocumentUrls,
                                 jstring jSitekey,
                                 jboolean jSpecificOnly)
{
    // 1. map jni types to std types
    auto url = JniJavaToStdString(env, jUrl);
    auto contentTypes = 1; // for simplicity (not mapped from `jContentTypes`)
    auto documentUrls = JavaStringListToStringVector(env, jDocumentUrls);
    auto sitekey = JniJavaToStdString(env, jSitekey);
    auto specificOnly = (jSpecificOnly == JNI_TRUE);

    // 2. process
    auto filter = engine.matches(url, contentTypes, documentUrls, sitekey, specificOnly);

    // 3. map back from std to jni type
    jobject jFilter = env->NewObject(blockingFilterClass->Get(), blockingFilterCtor, filter);

    // Warning: that causes memory leak as filter is never released.
    // Consider it not significant for benchmarking for now.
    return jFilter;
}

static jobject SubscriptionsToArrayList(JNIEnv* env, std::vector<std::string>& subscriptions)
{
    jobject list = NewJniArrayList(env);

    for (auto &it : subscriptions)
    {
        JniAddObjectToList(env, list, JniStdStringToJava(env, it));
    }

    return list;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_eyeo_ctu_Engine_getListedSubscriptions(JNIEnv *env, jobject thiz) {
    // here we want to measure only de-/marshalling costs,
    // so consider listedSubscriptions ready to exclude from measurement
    // (populate it during onLoad())
    return SubscriptionsToArrayList(env, listedSubscriptions);
}

static void matches(jbyte* requestBuffer, jsize requestBufferSize, jbyte* responseBuffer)
{
    // 1. deserialize request
    com::eyeo::ctu::engine::MatchesRequest request;
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
    com::eyeo::ctu::engine::MatchesResponse response;
    response.mutable_filter()->set_pointer((uint64_t)filter->pointer());

    int size = response.ByteSizeLong();
    response.SerializeToArray(responseBuffer, size);
}

// warning: use protobuf descriptors or update if `MatchesResponse` is changed
#define RESPONSE_SIZE 4

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_eyeo_ctu_Engine_protoMatchesByteArray(JNIEnv *env, jobject thiz, jbyteArray jRequestByteArray)
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
Java_com_eyeo_ctu_Engine_protoMatchesByteBuffer(JNIEnv *env, jobject thiz, jobject jRequestByteBuffer)
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