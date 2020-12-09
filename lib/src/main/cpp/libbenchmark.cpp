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

// ---

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

// 1 argument

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeIntArgNoResult(JNIEnv *env, jobject thiz, jint arg)
{
    // int argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeFloatArgNoResult(JNIEnv *env, jobject thiz, jfloat arg)
{
    // float argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeDoubleArgNoResult(JNIEnv *env, jobject thiz, jdouble arg)
{
    // double argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeStringArgNoResult(JNIEnv *env, jobject thiz, jstring arg)
{
    // string argument
    // no result
}

// 2 arguments

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_native2IntArgNoResult(JNIEnv *env, jobject thiz, jint arg1, jint arg2)
{
    // 2 int arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_native2FloatArgNoResult(JNIEnv *env, jobject thiz, jfloat arg1, jfloat arg2)
{
    // 2 float arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_native2DoubleArgNoResult(JNIEnv *env, jobject thiz, jdouble arg1, jdouble arg2)
{
    // 2 double arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_native2StringArgNoResult(JNIEnv *env, jobject thiz, jstring arg1, jstring arg2)
{
    // 2 string arguments
    // no result
}