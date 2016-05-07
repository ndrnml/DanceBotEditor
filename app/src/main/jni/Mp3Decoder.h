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

JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_initialize
        (JNIEnv*, jobject);

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_open
        (JNIEnv*, jobject self, jstring);

JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_decode
        (JNIEnv*, jobject, jlong);

JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_transfer
        (JNIEnv*, jobject, jlong, jshortArray);

JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_cleanUp
        (JNIEnv*, jobject, jlong);

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_checkFormat
        (JNIEnv*, jobject self, jstring);

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_getSampleRate
        (JNIEnv*, jobject, jlong);

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_getNumberOfSamples
        (JNIEnv*, jobject, jlong);

#ifdef __cplusplus
}
#endif

#endif