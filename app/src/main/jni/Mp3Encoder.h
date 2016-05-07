//
// Created by andrin on 24.10.15.
//

#include <jni.h>

#ifndef DANCEBOTEDITOR_MP3ENCODER_H
#define DANCEBOTEDITOR_MP3ENCODER_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_net_lame_LameEncoder_init
        (JNIEnv*, jclass, jint, jint, jint, jint, jint, jstring, jstring, jstring, jstring, jstring);

JNIEXPORT jint JNICALL Java_net_lame_LameEncoder_encode
        (JNIEnv*, jclass, jint, jshortArray, jshortArray, jint, jbyteArray);

JNIEXPORT jint JNICALL Java_net_lame_LameEncoder_encodeBufferInterleaved
        (JNIEnv*, jclass, jint, jshortArray, jint, jbyteArray);

JNIEXPORT jint JNICALL Java_net_lame_LameEncoder_flush
        (JNIEnv*, jclass, jint, jbyteArray);

JNIEXPORT void JNICALL Java_net_lame_LameEncoder_close
        (JNIEnv*, jclass, jint);

#ifdef __cplusplus
}
#endif

#endif //DANCEBOTEDITOR_MP3ENCODER_H
