#include <stdlib.h>
#include <android/log.h>

#include "Mp3Decoder.h"

static const char* LOG_TAG = "NATIVE_MP3_DECODER";

static Mp3Decoder m_Mp3Decoder;

/**
 * TODO comment
 */
Mp3Decoder::Mp3Decoder() :
    m_mh(NULL)
{}

Mp3Decoder::~Mp3Decoder() {
    // It's to late for error checks here
    mpg123_close(m_mh);
    mpg123_delete(m_mh);
    mpg123_exit();
}

/**
 * TODO comment
 */
JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_initialize
        (JNIEnv *env, jobject self)
{
    // Initialize mpg123 library
    return mpg123_init();
}

/**
 * TODO
 */
JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_open
        (JNIEnv *env, jobject self, jstring path_to_file)
{

    // Error code
    int err = MPG123_OK;

    // Properties
    int channels;
    long rate;
    long num_samples;
    int encoding;
    size_t buffer_size;

    // Create new mpg123 handle
    m_Mp3Decoder.m_mh = mpg123_new(NULL, &err);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mpg123_new: %p", m_Mp3Decoder.m_mh);

    if (err == MPG123_OK && m_Mp3Decoder.m_mh != NULL) {

        // Get the utf-8 string
        const char *file_path = env->GetStringUTFChars(path_to_file, JNI_FALSE);

        // Create new sound file and assign it to private Mp3Decoder field
        SoundFile *sound_file = new SoundFile(file_path);

        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file path: %s",
                            sound_file->file_path);

        err = mpg123_open(m_Mp3Decoder.m_mh, sound_file->file_path);

        // The jni string can now be released
        //env->ReleaseStringUTFChars(path_to_file, file_path);

        if (err == MPG123_OK) {

            err = mpg123_getformat(m_Mp3Decoder.m_mh, &rate, &channels, &encoding);

            if (err == MPG123_OK) {

                // Reset internal format table and only allow specific encodings
                mpg123_format_none(m_Mp3Decoder.m_mh);

                // TODO: remove this: Force 32 bit float encoding
                //mp3->encoding = MPG123_ENC_FLOAT_32;
                encoding = MPG123_ENC_SIGNED_16;

                // Set fixed format
                mpg123_format(m_Mp3Decoder.m_mh, rate, channels, encoding);

                // Store the maximum buffer size that is possible
                // The buffer will be needed in the reading/decoding step
                buffer_size = mpg123_outblock(m_Mp3Decoder.m_mh);

                // Store number of samples of one channel of current track
                num_samples = mpg123_length(m_Mp3Decoder.m_mh);

                // TODO: if sound file init fails, what to do?
                // Everything was properly loaded with mpg123. Initialize sound file
                sound_file->init(channels, rate, num_samples, encoding, buffer_size);

                //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "return sound file pointer: %p",
                //                    sound_file);
                return (jlong) sound_file;

            } else {

                __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_getformat err: %i",
                                    err);
                __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Trouble with mpg123: %s",
                                    mpg123_strerror(m_Mp3Decoder.m_mh));
            }

        } else {

            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_open err: %i", err);
        }

        delete sound_file;
    }

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG,
                        "Error: no proper initialization of mpg123lib.");
    return 0;
}


/**
 * TODO: The hole method is fixed to short encoding
 */
JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_decode
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    // Load sound file TODO: Check for NULL?
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    // Load pcm buffer
    short* dest = sound_file->pcm_buffer;

    int err = MPG123_OK;

    // TODO: casting? int to long
    long total_samples = sound_file->num_samples * sound_file->channels;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "total number of samples to process: %li", total_samples);

    int idx = 0;

    // TODO: For some songs processed number of samples does not match expected number to process
    while (idx != total_samples && err == MPG123_OK)
    {
        if (sound_file->left_samples <= 0)
        {
            size_t done = 0;
            err = mpg123_read( m_Mp3Decoder.m_mh, sound_file->buffer, sound_file->buffer_size, &done );

            // TODO: this is fixed to short encoding (MPG123_ENC_SIGNED_16)
            sound_file->left_samples = done / sizeof(short);
            sound_file->offset = 0;

        }
        else
        {
            short* src = ((short*)sound_file->buffer) + sound_file->offset;

            for( ; idx < total_samples && sound_file->offset < sound_file->buffer_size / sizeof(short); sound_file->left_samples--, sound_file->offset++, dest++, src++, idx++ )
            {
                *dest = *src;
            }
        }

        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %i samples", idx);
    }

    if (err == MPG123_DONE)
    {

        // Interleave left and right channel
        sound_file->interleaveChannels();


        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %i samples", idx);
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %u bytes", idx * sizeof(short));

        return idx;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: decoding failed");

        return 0;
    }
}


JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_transfer
        (JNIEnv *env, jobject self, jlong sound_file_handle, jshortArray pcm)
{

    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    jshort* j_pcm = env->GetShortArrayElements(pcm, NULL);

    long num_samples = sound_file->num_samples;

    int idx;

    for (idx = 0; idx < num_samples; ++idx)
    {
        j_pcm[idx] = sound_file->music_buffer[idx];
    }
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Transferred all samples");

    env->ReleaseShortArrayElements(pcm, j_pcm, 0);

    return idx;
}

/**
 *
 */
JNIEXPORT jint JNICALL Java_de_mpg123_MPG123Decoder_cleanUp
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    delete sound_file;
}

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_checkFormat
        (JNIEnv *env, jobject self, jstring path_to_file)
{

    // Error code
    int err = MPG123_OK;

    // Properties
    int channels;
    long rate;
    long num_samples;
    int encoding;
    size_t buffer_size;

    mpg123_handle* mh;

    // Create new mpg123 handle
    mh = mpg123_new(NULL, &err);
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "mpg123_new: %p", mh);

    if (err == MPG123_OK && mh != NULL) {

        // Get the utf-8 string
        const char *file_path = env->GetStringUTFChars(path_to_file, JNI_FALSE);
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Sound file path: %s", file_path);

        err = mpg123_open(mh, file_path);

        if (err == MPG123_OK) {

            err = mpg123_getformat(mh, &rate, &channels, &encoding);

            if (err == MPG123_OK) {

                return MPG123_OK;

            } else {

                __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Error: mpg123_getformat err: %i",
                                    err);
                __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Trouble with mpg123: %s",
                                    mpg123_strerror(mh));

                return MPG123_ERR;
            }

        } else {

            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Error: mpg123_open err: %i", err);

            return MPG123_ERR;
        }
    }

    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "Error: no proper initialization of mpg123lib.");
    return MPG123_ERR;
}

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_getSampleRate
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->rate;
}

JNIEXPORT jlong JNICALL Java_de_mpg123_MPG123Decoder_getNumberOfSamples
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->num_samples;
}