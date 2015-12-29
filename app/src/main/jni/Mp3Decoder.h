#ifndef MP3_DECODER_H_
#define MP3_DECODER_H_

#include <jni.h>
#include <mpg123/mpg123.h>

#include "SoundFile.h"

class Mp3Decoder {

public:

    Mp3Decoder();
    ~Mp3Decoder();

    mpg123_handle* m_mh;
};

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_initialize
        (JNIEnv*, jobject);

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_open
        (JNIEnv*, jobject self, jstring);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_decode
        (JNIEnv*, jobject, jlong);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_transfer
        (JNIEnv*, jobject, jlong, jshortArray);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_delete
        (JNIEnv*, jobject, jlong);

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_checkFormat
        (JNIEnv*, jobject self, jstring);

#ifdef __cplusplus
}
#endif

#endif