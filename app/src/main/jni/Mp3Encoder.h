//
// Created by andrin on 24.10.15.
//

#include <jni.h>

#ifndef DANCEBOTEDITOR_MP3ENCODER_H
#define DANCEBOTEDITOR_MP3ENCODER_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Encoder_init
        (JNIEnv*, jclass, jint, jint, jint, jint, jint, jstring, jstring, jstring, jstring, jstring);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Encoder_encode
        (JNIEnv*, jclass, jint, jshortArray, jshortArray, jint, jbyteArray);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Encoder_encodeBufferInterleaved
        (JNIEnv*, jclass, jint, jshortArray, jint, jbyteArray);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Encoder_flush
        (JNIEnv*, jclass, jint, jbyteArray);

JNIEXPORT void JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Encoder_close
        (JNIEnv*, jclass, jint);

#ifdef __cplusplus
}
#endif

#endif //DANCEBOTEDITOR_MP3ENCODER_H
