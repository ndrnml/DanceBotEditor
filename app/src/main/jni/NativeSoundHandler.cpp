#include <jni.h>
#include <android/log.h>

#include "NativeSoundHandler.h"

static NativeSoundHandler *soundHandler = NULL;

int NativeSoundHandler::init()
{
	__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialize native sound handler...");

    // Retrieve absolute file path
	const char* music_file_path = env->GetStringUTFChars(musicFilePath, JNI_FALSE);

	// Get starting address of int buffer where the extracted beats will be stored
    int* beat_buffer = (int*)env->GetDirectBufferAddress(intBuffer);

    // Pass the length of the fixed size beat buffer (int buffer)
    int beat_buffer_size = beatBufferSize;

    // Create new music file
    SoundFile sndFile = SoundFile(music_file_path);

    // Create new decoder object and initialize it
    // The music file gets decoded to PCM and then passed to the beat extraction
    Mp3Decoder decoder = Mp3Decoder();
    int err = decoder.init(sndFile);

    if (err < 0)
    {
        // TODO
    }
    else
    {
        // TODO
    }

    // Decode sound file to PCM
    int samples = decoder.decode();

    if (samples > 0)
    {
        // TODO
    }
    else
    {
        // TODO
    }

    return NO_ERROR;
}


/**
 * Make C++ functions unique and visible to C
 */
extern "C" {
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeInitSoundHandler(JNIEnv *env, jobject self);
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeExtractBeats(JNIEnv *env, jobject self, jstring musicFilePath, jobject intBuffer, jint intBufferSize);
	JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeEncodeToMP3(JNIEnv *env, jobject self, jobject byteBuffer, jobject params);
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeCleanUpSoundHandler(JNIEnv *env, jobject self);
}


/**
 * The decoded music file data is only temporarily and will be deleted after the function call
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeExtractBeats(JNIEnv *env, jobject self, jstring musicFilePath, jobject intBuffer, jint intBufferSize)
{

    // Create new beat extractor and extract beats
    BeatExtractor beatExtractor = BeatExtractor();
    int beats = beatExtractor.extractBeats(sndFile, beat_buffer, beatBuffer_size);

    if (beats > 0)
    {
        // TODO
    }
    else
    {
        // TODO
    }

    return NO_ERROR;
}


/**
 * TODO: blabla
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeEncodeToMP3(JNIEnv* env, jobject self, jobject byteBuffer, jstring filePath)
{

    // TODO comment
    unsigned char* byte_buffer = (unsigned char*)env->GetDirectBufferAddress(byteBuffer);

    int err = soundHandler->pcmToMp3(path, byte_buffer);

    // Encode to MP3
    // TODO: int beats = beatExtractor.extractBeats(sndFile, beat_buffer, beatBuffer_size);

    if (beats > 0)
    {
        // TODO
    }
    else
    {
        // TODO
    }

    return NO_ERROR;
}


/**
 * TODO: blabla
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeCleanUpSoundHandler(JNIEnv* env, jobject self)
{

    return NO_ERROR;
}