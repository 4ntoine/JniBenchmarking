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
#include "Calculator.h"

Calculator calculator;

extern "C"
JNIEXPORT jfloat JNICALL
    Java_com_eyeo_ctu_Calculator_nativeAdd(
        JNIEnv *env,
        jobject thiz,
        jfloat a,
        jfloat b)
{
    return calculator.add(a, b);
}

extern "C"
JNIEXPORT jfloat JNICALL
    Java_com_eyeo_ctu_Calculator_nativeTimesAdd(
        JNIEnv *env,
        jobject thiz,
        jint times,
        jfloat a,
        jfloat b)
{
    jfloat result = 0;
    while (times--) {
        result = calculator.add(a, b);
    }
    return result; // it seems to have proper comparision with `nativeAdd` we need to return smth
}