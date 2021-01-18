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
#include <Utils.h>

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
jnaNativeNoArgsNoResult()
{
    // no arguments
    // no result
}

extern "C"
JNIEXPORT jint JNICALL
jnaNativeNoArgsIntResult()
{
    // no argument
    // int result
    return 1;
}

extern "C"
JNIEXPORT jfloat JNICALL
jnaNativeNoArgsFloatResult()
{
    // no argument
    // float result
    return 1.0;
}

extern "C"
JNIEXPORT jdouble JNICALL
jnaNativeNoArgsDoubleResult()
{
    // no argument
    // double result
    return 1.0;
}

// 1 argument

extern "C"
JNIEXPORT void JNICALL
jnaNativeIntArgNoResult(jint arg)
{
    // int argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNativeFloatArgNoResult(jfloat arg)
{
    // float argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNativeDoubleArgNoResult(jdouble arg)
{
    // double argument
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNativeStringArgNoResult(jstring arg)
{
    // string argument
    // no result
}

// 2 arguments

extern "C"
JNIEXPORT void JNICALL
jnaNative2IntArgNoResult(jint arg1, jint arg2)
{
    // 2 int arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNative2FloatArgNoResult(jfloat arg1, jfloat arg2)
{
    // 2 float arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNative2DoubleArgNoResult(jdouble arg1, jdouble arg2)
{
    // 2 double arguments
    // no result
}

extern "C"
JNIEXPORT void JNICALL
jnaNative2StringArgNoResult(jstring arg1, jstring arg2)
{
    // 2 string arguments
    // no result
}

// echo

extern "C"
JNIEXPORT jint JNICALL
jnaNativeIntEcho(jint arg)
{
    return arg;
}

extern "C"
JNIEXPORT jfloat JNICALL
jnaNativeFloatEcho(jfloat arg)
{
    return arg;
}

extern "C"
JNIEXPORT jdouble JNICALL
jnaNativeDoubleEcho(jdouble arg)
{
    return arg;
}

extern "C"
JNIEXPORT jstring JNICALL
jnaNativeStringEcho(jstring arg)
{
    return arg;
}