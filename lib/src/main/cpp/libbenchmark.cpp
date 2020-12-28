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

// precaching
JniGlobalReference<jclass>* libraryClassRef = nullptr;
jmethodID concreteMethod = nullptr;
jmethodID ifaceMethod = nullptr;
jmethodID abstractMethod = nullptr;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return JNI_ERR;
    }

    JniUtils_OnLoad(vm, env, reserved);

    auto libraryClass = env->FindClass("com/eyeo/ctu/Library");
    libraryClassRef = new JniGlobalReference<jclass>(env, libraryClass);
    auto ifaceClass = env->FindClass("com/eyeo/ctu/Iface");
    auto abstractClass = env->FindClass("com/eyeo/ctu/Base");

    concreteMethod = env->GetMethodID(libraryClass, "concreteMethod", "()V");
    ifaceMethod = env->GetMethodID(ifaceClass, "ifaceMethod", "()V");
    abstractMethod = env->GetMethodID(abstractClass, "abstractMethod", "()V");

    return JNI_VERSION;
}

void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK)
    {
        return;
    }

    if (libraryClassRef)
    {
        delete libraryClassRef;
        libraryClassRef = nullptr;
    }
    JniUtils_OnUnload(vm, env, reserved);
}

// ---

std::string stringResult = "hello world";

// === JNA ===

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

// === JNI ===

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
Java_com_eyeo_ctu_Library_nativeThreadSafeNoArgsNoResult(JNIEnv *env, jobject thiz)
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

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeNoArgsNoResultAllocateString(JNIEnv *env, jobject thiz)
{
    // no argument
    // no result
    // but allocating the string
    auto tmpString = JniStdStringToJava(env, stringResult);
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
Java_com_eyeo_ctu_JnaLibrary_native2IntArgNoResult(jint arg1, jint arg2)
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

// echo

extern "C"
JNIEXPORT jint JNICALL
Java_com_eyeo_ctu_Library_nativeIntEcho(JNIEnv *env, jobject thiz, jint arg)
{
    return arg;
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_eyeo_ctu_Library_nativeFloatEcho(JNIEnv *env, jobject thiz, jfloat arg)
{
    return arg;
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_eyeo_ctu_Library_nativeDoubleEcho(JNIEnv *env, jobject thiz, jdouble arg)
{
    return arg;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_eyeo_ctu_Library_nativeStringEcho(JNIEnv *env, jobject thiz, jstring arg)
{
    return arg;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eyeo_ctu_Library_nativeFindClass(JNIEnv *env, jobject thiz, jstring jClassName)
{
    auto className = JniJavaToStdString(env, jClassName);
    auto clazz = env->FindClass(className.c_str());
    return (clazz ? JNI_TRUE : JNI_FALSE);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eyeo_ctu_Library_nativeFindClassAndMethod(JNIEnv *env, jobject thiz,
                                                   jstring jClassName,
                                                   jstring jMethodName,
                                                   jstring jMethodSignature)
{
    auto className = JniJavaToStdString(env, jClassName);
    auto methodName = JniJavaToStdString(env, jMethodName);
    auto signature = JniJavaToStdString(env, jMethodSignature);

    auto clazz = env->FindClass(className.c_str());
    if (!clazz)
        return JNI_FALSE;

    auto method = env->GetMethodID(clazz, methodName.c_str(), signature.c_str());
    return (method ? JNI_TRUE : JNI_FALSE);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeCallJavaFromNativeAsConcrete(JNIEnv *env, jobject thiz)
{
    env->CallVoidMethod(thiz, concreteMethod);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eyeo_ctu_Library_nativeCallJavaFromNative(JNIEnv *env,
                                                   jobject thiz,
                                                   jobject obj,
                                                   jstring jMethodName,
                                                   jstring jMethodSignature)
{
    auto methodName = JniJavaToStdString(env, jMethodName);
    auto signature = JniJavaToStdString(env, jMethodSignature);

    auto clazz = env->GetObjectClass(obj);
    auto method = env->GetMethodID(clazz, methodName.c_str(), signature.c_str());
    if (!method)
        return JNI_FALSE;

    env->CallVoidMethod(obj, method);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeCallJavaFromNativeAsInterface(JNIEnv *env, jobject thiz)
{
    env->CallVoidMethod(thiz, ifaceMethod);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_eyeo_ctu_Library_nativeCallJavaFromNativeAsAbstract(JNIEnv *env, jobject thiz)
{
    env->CallVoidMethod(thiz, abstractMethod);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_eyeo_ctu_Library_nativeFindMethod(JNIEnv *env,
                                           jobject thiz,
                                           jstring jMethodName,
                                           jstring jMethodSignature)
{
    auto methodName = JniJavaToStdString(env, jMethodName);
    auto signature = JniJavaToStdString(env, jMethodSignature);

    auto method = env->GetMethodID(libraryClassRef->Get(), methodName.c_str(), signature.c_str());
    return (method ? JNI_TRUE : JNI_FALSE);
}