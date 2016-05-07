#ifndef BEAT_EXTRACTOR_H_
#define BEAT_EXTRACTOR_H_

#include <jni.h>

class BeatExtractor {
public:
        static long num_processed_samples;
};

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_extractBeats
        (JNIEnv*, jobject, jlong, jobject, jint);

JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_extract(
        JNIEnv*, jobject, jlong, jobject, jint, jlong, jlong);

JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_cleanUp
        (JNIEnv*, jobject, jlong);

JNIEXPORT jlong JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_getProcessedSamples
        (JNIEnv*, jobject);

JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_getNumBeatsDetected
        (JNIEnv*, jobject, jlong);

#ifdef __cplusplus
}
#endif

#endif