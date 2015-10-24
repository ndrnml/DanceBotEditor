#include <jni.h>
#include <android/log.h>

#include "NativeSoundHandler.h"

static const char* LOG_TAG = "NATIVE_SOUND_HANDLER";

static NativeSoundHandler soundHandler;

/**
 * Constructor
 */
NativeSoundHandler::NativeSoundHandler() :
    m_sound_file(NULL)
{}

/**
 * TODO: comment
 */
NativeSoundHandler::~NativeSoundHandler()
{
    delete m_sound_file;
}

/**
 * Initialize sound handler based on (new) file
 */
int NativeSoundHandler::initAndDecodeSoundFile(const char* music_file_path)
{
	__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialize native sound handler...");

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "path to sound file: %s", music_file_path);

    // TODO: SNDFILE Create new music file
    m_sound_file = new SoundFile(music_file_path);

    // Create new decoder object and initialize it
    // The music file gets decoded to PCM and then passed to the beat extraction
    //m_mp3_decoder = Mp3Decoder();
    int err = m_mp3_decoder.init(m_sound_file);

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
 * Perform beat extraction on decoded sound file
 */
int NativeSoundHandler::extractBeats(int* beat_buffer, int beat_buffer_size)
{

    // Create new beat extractor and extract beats
    int beats = m_beat_extractor.extractBeats(m_sound_file, beat_buffer, beat_buffer_size);

    if (beats > 0)
    {
        return NO_ERROR;
    }

    return ERROR;
}

/**
 * Return sample rate of current sound file
 */
int NativeSoundHandler::getSampleRate()
{
    if (m_sound_file != NULL)
    {
        return static_cast<int>(m_sound_file->rate);
    }
    else
    {
        return 0;
    }
}


/**
 * Return total number of samples of current sound file
 */
long NativeSoundHandler::getNumberOfSamples()
{
    if (m_sound_file != NULL)
    {
        return m_sound_file->num_samples;
    }
    else
    {
        return 0;
    }
}


/**
 * Return total number of detected beats
 */
int NativeSoundHandler::getNumberOfBeatsDetected()
{
    if (m_sound_file != NULL)
    {
        return m_beat_extractor.getNumBeatsDetected();
    }
    else
    {
        return 0;
    }
}


/**
 * Make C++ functions unique and visible to C
 */
extern "C" {
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeSoundHandlerInit(JNIEnv *env, jobject self, jstring musicFilePath);
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeExtractBeats(JNIEnv *env, jobject self, jobject intBuffer, jint intBufferSize);
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetSampleRate(JNIEnv *env, jobject self);
    JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetNumberOfSamples(JNIEnv *env, jobject self);
    JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetNumBeatsDetected(JNIEnv *env, jobject self);

    //JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeEncodeToMP3(JNIEnv *env, jobject self, jobject byteBuffer, jobject params);
    //JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_EditorActivity_NativeCleanUpSoundHandler(JNIEnv *env, jobject self);
}

/**
 * Initialize static sound handler
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeSoundHandlerInit(JNIEnv *env, jobject self, jstring musicFilePath)
{
    // Retrieve absolute file path
	const char* music_file_path = env->GetStringUTFChars(musicFilePath, JNI_FALSE);

    int err = soundHandler.initAndDecodeSoundFile(music_file_path);

    // TODO Not sure if this is necessary
    //javaEnvironment->ReleaseStringUTFChars(musicFilePath, music_file_path);

    // If decoding failed: err < 0, else err == 0
    return err;
}


/**
 * The decoded music file data is only temporarily available and will be deleted after the function call TODO: is that so?
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeExtractBeats(JNIEnv *env, jobject self, jobject intBuffer, jint intBufferSize)
{

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "enters NativeExtractBeats");

	// Get starting address of int buffer where the extracted beats will be stored
    int* beat_buffer = (int*)env->GetDirectBufferAddress(intBuffer);

    // Pass the length of the fixed size beat buffer (int buffer)
    int beat_buffer_size = intBufferSize;

    int err = soundHandler.extractBeats(beat_buffer, beat_buffer_size);

    // If beat extraction failed: err < 0, else err == 0
    return err;
}

/**
 * TODO
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetSampleRate(JNIEnv *env, jobject instance)
{
    int sampleRate = soundHandler.getSampleRate();
    return sampleRate;
}

/**
 * TODO
 */
JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetNumberOfSamples(JNIEnv *env, jobject instance)
{
    long numSamples = soundHandler.getNumberOfSamples();
    return numSamples;
}

/**
 * TODO
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_handlers_BeatExtractionHandler_NativeGetNumBeatsDetected(JNIEnv *env, jobject instance)
{
    int numBeats = soundHandler.getNumberOfBeatsDetected();
    return numBeats;
}


/**
 * TODO: comment
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