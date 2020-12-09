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
#include "Utils.h"

std::string stringResult = "hello world";

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeStaticNoArgsNoResult(JNIEnv *env, jclass claz)
{
    // static
    // no arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
    Java_com_eyeo_ctu_Library_nativeNoArgsNoResult(JNIEnv *env, jobject thiz)
{
    // no arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeThreadSafeNoArgsNoResult(
        JNIEnv *env,
        jobject thiz)
{
    // no arguments
    // no result
    JavaVM *vm;
    env->GetJavaVM(&vm);
    JNIEnvAcquire acquire(vm); // attach current thread
}

extern "C"
JNIEXPORT jint JNICALL
    Java_com_eyeo_ctu_Library_nativeNoArgsIntResult(JNIEnv *env, jobject thiz)
{
    // no argument
    // int result
    return 1;
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_eyeo_ctu_Library_nativeNoArgsFloatResult(JNIEnv *env, jobject thiz)
{
    // no argument
    // float result
    return 1.0;
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_eyeo_ctu_Library_nativeNoArgsDoubleResult(JNIEnv *env, jobject thiz)
{
    // no argument
    // double result
    return 1.0;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_eyeo_ctu_Library_nativeNoArgsStringResult(JNIEnv *env, jobject thiz)
{
    // no argument
    // string result
    return JniStdStringToJava(env, stringResult);
}

// ---

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return JNI_ERR;
    }

    JniUtils_OnLoad(vm, env, reserved);

    return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return;
    }

    JniUtils_OnUnload(vm, env, reserved);
}