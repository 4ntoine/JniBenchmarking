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
#include <Engine.h>
#include <Utils.h>

// precached in JNI_OnLoad and released in JNI_OnUnload
JniGlobalReference<jclass>* blockingFilterClass;
jmethodID blockingFilterCtor;
std::vector<std::string> listedSubscriptions;

void JniEngine_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved)
{
    blockingFilterClass = new JniGlobalReference<jclass>(env,
            env->FindClass("com/eyeo/ctu/engine/jni/BlockingFilter"));
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
Java_com_eyeo_ctu_engine_jni_Engine_matches(JNIEnv *env,
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
    jobject jFilter = env->NewObject(blockingFilterClass->Get(), blockingFilterCtor, filter->pointer());
    delete filter;

    return jFilter;
}