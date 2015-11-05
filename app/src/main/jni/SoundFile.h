#ifndef SOUND_FILE_H_
#define SOUND_FILE_H_

#include <jni.h>
#include <stdlib.h>

// TODO: replace by SNDFILE
class SoundFile {

public:

    SoundFile(const char* file_path_);
    ~SoundFile();

    int init(int channels_, long rate_, long num_samples_, int encoding_, size_t buffer_size_);
    int prepareForBeatExtraction();

    const char* file_path;
    int number_beats_detected;
    int channels;
    long rate;
    long num_samples;
    int encoding;
    size_t buffer_size;
    unsigned char* buffer;
    size_t left_samples;
    size_t offset;
    short* pcm_buffer;
    short* music_buffer;
};

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_getSampleRate
        (JNIEnv *env, jobject self, jlong sound_file_handle);

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_getNumberOfSamples
        (JNIEnv *env, jobject self, jlong sound_file_handle);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_getNumBeatsDetected
        (JNIEnv *env, jobject self, jlong sound_file_handle);

#ifdef __cplusplus
}
#endif
#endif