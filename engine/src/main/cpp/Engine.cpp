#include "Engine.h"
#include "Utils.h"

// precached in JNI_OnLoad and released in JNI_OnUnload
JniGlobalReference<jclass>* blockingFilterClass;
jmethodID blockingFilterCtor;

void JniEngine_OnLoad(JavaVM* vm, JNIEnv* env, void* reserved)
{
    blockingFilterClass = new JniGlobalReference<jclass>(env, env->FindClass("com/eyeo/ctu/BlockingFilter"));
    blockingFilterCtor = env->GetMethodID(blockingFilterClass->Get(), "<init>", "()V");
}

void JniEngine_OnUnload(JavaVM* vm, JNIEnv* env, void* reserved)
{
    if (blockingFilterClass)
    {
        delete blockingFilterClass;
        blockingFilterClass = nullptr;
    }
}

