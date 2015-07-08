#include <jni.h>
#include <android/log.h>

#include "NativeSoundHandler.h"

static const char* LOG_TAG = "NATIVE_SOUND_HANDLER";

static NativeSoundHandler soundHandler;

/**
 * Initialize sound handler based on (new) file
 */
int NativeSoundHandler::initAndDecodeSoundFile(const char* music_file_path)
{
	__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialize native sound handler...");

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "path to sound file: %s", music_file_path);

    // TODO: SNDFILE Create new music file
    //m_sound_file = SoundFile(music_file_path);
    m_sound_file.file_path = music_file_path;

    // Create new decoder object and initialize it
    // The music file gets decoded to PCM and then passed to the beat extraction
    //m_mp3_decoder = Mp3Decoder();
    int err = m_mp3_decoder.init(&m_sound_file);

    if (err < 0)
    {
        return ERROR;
    }

    // Decode sound file to PCM
    err = m_mp3_decoder.decode();

    if (err < 0)
    {
        return ERROR;
    }

    return NO_ERROR;
}


/**
 * Make C++ functions unique and visible to C
 */
extern "C" {
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeSoundHandlerInit(JNIEnv *env, jobject self, jstring musicFilePath);
    //JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeExtractBeats(JNIEnv *env, jobject self, jobject intBuffer, jint intBufferSize);
	//JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeEncodeToMP3(JNIEnv *env, jobject self, jobject byteBuffer, jobject params);
    //JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeCleanUpSoundHandler(JNIEnv *env, jobject self);
}

/**
 * Initialize static sound handler
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeSoundHandlerInit(JNIEnv *env, jobject self, jstring musicFilePath)
{
    // Retrieve absolute file path
	const char* music_file_path = env->GetStringUTFChars(musicFilePath, JNI_FALSE);

    // Create new sound handler object
    //soundHandler = NativeSoundHandler();

    int err = soundHandler.initAndDecodeSoundFile(music_file_path);

    if (err < 0)
    {
        return -1;
    }

    return 0;
}

/**
 * The decoded music file data is only temporarily and will be deleted after the function call
 */
/*JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeExtractBeats(JNIEnv *env, jobject self, jobject intBuffer, jint intBufferSize)
{

	// Get starting address of int buffer where the extracted beats will be stored
    int* beat_buffer = (int*)env->GetDirectBufferAddress(intBuffer);

    // Pass the length of the fixed size beat buffer (int buffer)
    int beat_buffer_size = beatBufferSize;

    // Interpolate raw audio data
    m_music_buffer = new short[mp3_length];

    for (int i = 0; i < mp3_length; ++i)
    {
        m_music_buffer[i] = (m_pcm_buffer[2*i] + m_pcm_buffer[2*i + 1]) / 2;
    }

    // Create new beat extractor and extract beats
    BeatExtractor beatExtractor = BeatExtractor();
    int beats = beatExtractor.extractBeats(snd_file, beat_buffer, beatBuffer_size);

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
*/

/**
 * TODO: blabla
 */
/*
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeEncodeToMP3(JNIEnv* env, jobject self, jobject byteBuffer, jstring filePath)
{

    // TODO comment
    unsigned char* byte_buffer = (unsigned char*)env->GetDirectBufferAddress(byteBuffer);

    int err = soundHandler->pcmToMp3(path, byte_buffer);

    // Encode to MP3
    // TODO: int beats = beatExtractor.extractBeats(snd_file, beat_buffer, beatBuffer_size);

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
*/

/**
 * TODO: blabla
 */
/*JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeCleanUpSoundHandler(JNIEnv* env, jobject self)
{
    // TODO
    return NO_ERROR;
}
*/