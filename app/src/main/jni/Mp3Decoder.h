#ifndef MP3_DECODER_H_
#define MP3_DECODER_H_

#include <jni.h>
#include <mpg123/mpg123.h>

#include "SoundFile.h"

class Mp3Decoder {

public:

    Mp3Decoder();
    ~Mp3Decoder() {};

    void cleanup();

    mpg123_handle* m_mh;
    SoundFile* m_snd_file;

};

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_initialize
        (JNIEnv *env, jobject self);

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_open
        (JNIEnv *env, jobject self, jstring path_to_file);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_decode
        (JNIEnv *env, jobject self, jlong sound_file_handle);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_delete
        (JNIEnv *env, jobject self, jlong sound_file_handle);

#ifdef __cplusplus
}
#endif

#endif