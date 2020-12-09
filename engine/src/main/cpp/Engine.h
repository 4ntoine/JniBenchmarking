#ifndef ENGINE_H
#define ENGINE_H

#include <jni.h>

void JniEngine_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved);

void JniEngine_OnUnload(JavaVM* vm, JNIEnv* env, void* reserved);

#endif ENGINE_H