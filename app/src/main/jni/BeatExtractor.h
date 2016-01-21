#ifndef BEAT_EXTRACTOR_H_
#define BEAT_EXTRACTOR_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_BeatExtractor_extractBeats
        (JNIEnv*, jobject, jlong, jobject, jint);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_BeatExtractor_extract(
        JNIEnv*, jobject, jlong, jobject, jint, jlong, jlong);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_BeatExtractor_cleanUp
        (JNIEnv*, jobject, jlong);

#ifdef __cplusplus
}
#endif

#endif