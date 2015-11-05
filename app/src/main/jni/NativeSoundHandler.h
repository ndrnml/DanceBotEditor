#ifndef NATIVE_SOUND_HANDLER_H_
#define NATIVE_SOUND_HANDLER_H_

#include <jni.h>

#include "SoundFile.h"
#include "Mp3Decoder.h"
//#include "Mp3Encoder.h"
#include "BeatExtractor.h"

class NativeSoundHandler {

    static const int NO_ERROR = 0;
    static const int ERROR = -1;

public:

    NativeSoundHandler();
    ~NativeSoundHandler();

    int initSoundFile(const char* file_path);
    int decode();
    int extractBeats(int* beat_buffer, int beat_buffer_size);

    int getSampleRate();
    long getNumberOfSamples();
    int getNumberOfBeatsDetected();

private:

    SoundFile* m_sound_file;
    Mp3Decoder m_mp3_decoder;
    //Mp3Encoder m_mp3_encoder;
    BeatExtractor m_beat_extractor;

};

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_load
        (JNIEnv *env, jobject self, jstring path_to_file);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_decode
        (JNIEnv *env, jobject self);

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_sampleRate
        (JNIEnv *env, jobject self);

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_numberOfSamples
        (JNIEnv *env, jobject self);

#ifdef __cplusplus
}
#endif

#endif