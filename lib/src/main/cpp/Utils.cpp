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
#include <sstream>
#include <iostream>

#include "Utils.h"

// precached in JNI_OnLoad and released in JNI_OnUnload
JniGlobalReference<jclass>* arrayListClass;
jmethodID  arrayListCtor;

void JniUtils_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved)
{
  arrayListClass = new JniGlobalReference<jclass>(env, env->FindClass("java/util/ArrayList"));
  arrayListCtor = env->GetMethodID(arrayListClass->Get(), "<init>", "()V");
}

void JniUtils_OnUnload(JavaVM* vm, JNIEnv* env, void* reserved)
{
  if (arrayListClass)
  {
    delete arrayListClass;
    arrayListClass = NULL;
  }
}

std::string JniJavaToStdString(JNIEnv* env, jstring str)
{
  if (!str)
  {
    return std::string();
  }

  const char* cStr = env->GetStringUTFChars(str, 0);
  std::string ret(cStr);
  env->ReleaseStringUTFChars(str, cStr);

  return ret;
}

jstring JniStdStringToJava(JNIEnv* env, std::string str)
{
  return env->NewStringUTF(str.c_str());
}

bool stringBeginsWith(const std::string& string, const std::string& beginning)
{
  return string.compare(0, beginning.length(), beginning) == 0;
}

jobject NewJniArrayList(JNIEnv* env)
{
  return env->NewObject(arrayListClass->Get(), arrayListCtor);
}

jmethodID JniGetAddToListMethod(JNIEnv* env, jobject list)
{
  JniLocalReference<jclass> clazz(env, env->GetObjectClass(list));
  return env->GetMethodID(*clazz, "add", "(Ljava/lang/Object;)Z");
}

jmethodID JniGetListSizeMethod(JNIEnv* env, jobject list)
{
  JniLocalReference<jclass> clazz(env, env->GetObjectClass(list));
  return env->GetMethodID(*clazz, "size", "()I");
}

jint JniGetListSize(JNIEnv* env, jobject list, jmethodID getSizeMethod)
{
  return env->CallIntMethod(list, getSizeMethod);
}

jmethodID JniGetGetFromListMethod(JNIEnv* env, jobject list)
{
  JniLocalReference<jclass> clazz(env, env->GetObjectClass(list));
  return env->GetMethodID(*clazz, "get", "(I)Ljava/lang/Object;");
}

void JniAddObjectToList(JNIEnv* env, jobject list, jmethodID addMethod, jobject value)
{
  env->CallBooleanMethod(list, addMethod, value);
}

jobject JniGetObjectFromList(JNIEnv* env, jobject list, jmethodID getMethod, jint i)
{
  return env->CallObjectMethod(list, getMethod, i);
}

void JniAddObjectToList(JNIEnv* env, jobject list, jobject value)
{
  jmethodID addMethod = JniGetAddToListMethod(env, list);
  JniAddObjectToList(env, list, addMethod, value);
}

JNIEnvAcquire::JNIEnvAcquire(JavaVM* javaVM)
  : javaVM(javaVM), jniEnv(0), attachmentStatus(0)
{
  attachmentStatus = javaVM->GetEnv((void **)&jniEnv, JNI_VERSION);
  if (attachmentStatus == JNI_EDETACHED)
  {
    if (javaVM->AttachCurrentThread(&jniEnv, 0))
    {
      // This one is FATAL, we can't recover from this (because without a JVM we're dead), so
      // throwing a runtime_exception in a ctor can be tolerated here IMHO
      throw std::runtime_error("Failed to get JNI environment");
    }
  }
}

JNIEnvAcquire::~JNIEnvAcquire()
{
  if (attachmentStatus == JNI_EDETACHED)
  {
    javaVM->DetachCurrentThread();
  }
}

template<typename T>
static jobject NewJniObject(JNIEnv* env, T&& value, jclass clazz, jmethodID ctor)
{
  return env->NewObject(clazz, ctor, JniPtrToLong(new T(std::forward<T>(value))));
}
