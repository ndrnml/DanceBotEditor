#ifndef BEAT_EXTRACTOR_H_
#define BEAT_EXTRACTOR_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_BeatExtractor_extractBeats
        (JNIEnv *env, jobject self, jlong soundFileHandle, jobject intBuffer, jint intBufferSize);

#ifdef __cplusplus
}
#endif

#endif