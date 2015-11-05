#include <stdlib.h>
#include <android/log.h>
#include <jni.h>

#include "Mp3Decoder.h"

static const char* LOG_TAG = "NATIVE_MP3_DECODER";

static Mp3Decoder m_Mp3Decoder;

/**
 * TODO comment
 */
Mp3Decoder::Mp3Decoder() :
    m_mh(NULL),
    m_snd_file(NULL)
{}

void Mp3Decoder::cleanup() {

    // Delete sound file
    delete m_snd_file;

    // It's to late for error checks here
    mpg123_close(m_mh);
    mpg123_delete(m_mh);
    mpg123_exit();
}


/**
 * TODO comment
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_initialize(JNIEnv *env, jobject self)
{
    // Initialize mpg123 library
    return mpg123_init();
}

/**
 * TODO
 */
JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_open(JNIEnv *env, jobject self, jstring path_to_file)
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
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mpg123_new: %d", m_Mp3Decoder.m_mh);

    if (err == MPG123_OK && m_Mp3Decoder.m_mh != NULL)
    {
        // Get the utf-8 string
        const char* file_path = env->GetStringUTFChars(path_to_file, NULL);

        // Create new sound file and assign it to private Mp3Decoder field
        m_Mp3Decoder.m_snd_file = new SoundFile(file_path);

        // The jni string can now be released
        env->ReleaseStringUTFChars(path_to_file, file_path);

        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file path: %s", m_Mp3Decoder.m_snd_file->file_path);

        err = mpg123_open(m_Mp3Decoder.m_mh, m_Mp3Decoder.m_snd_file->file_path);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_open err: %i", err);
            m_Mp3Decoder.cleanup();
            return err;
        }

        err = mpg123_getformat(m_Mp3Decoder.m_mh, &rate, &channels, &encoding);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_getformat err: %i", err);
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Trouble with mpg123: %s", mpg123_strerror(m_Mp3Decoder.m_mh));
            m_Mp3Decoder.cleanup();
            return err;
        }

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
        m_Mp3Decoder.m_snd_file->init(channels, rate, num_samples, encoding, buffer_size);

    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: no proper initialization of mpg123lib.");
        m_Mp3Decoder.cleanup();
        return 0;
    }

    return (jlong)m_Mp3Decoder.m_snd_file;
}

/**
 * TODO: The hole method is fixed to short encoding
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_decode(JNIEnv *env, jobject self, jlong sound_file_handle)
{
    // Load pcm buffer
    short* dest = m_Mp3Decoder.m_snd_file->pcm_buffer;

    int err = MPG123_OK;

    // TODO: casting? int to long
    long total_samples = m_Mp3Decoder.m_snd_file->num_samples * m_Mp3Decoder.m_snd_file->channels;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mp3 number of samples: %li", m_Mp3Decoder.m_snd_file->num_samples);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "total number of samples to process: %li", total_samples);

    int idx = 0;
    // TODO: For some songs processed number of samples does not match expected number to process
    while (idx != total_samples && err == MPG123_OK)
    {
        if (m_Mp3Decoder.m_snd_file->left_samples <= 0)
        {
            size_t done = 0;
            err = mpg123_read( m_Mp3Decoder.m_mh, m_Mp3Decoder.m_snd_file->buffer, m_Mp3Decoder.m_snd_file->buffer_size, &done );

            // TODO: this is fixed to short encoding (MPG123_ENC_SIGNED_16)
            m_Mp3Decoder.m_snd_file->left_samples = done / sizeof(short);
            m_Mp3Decoder.m_snd_file->offset = 0;

        }
        else
        {
            short* src = ((short*)m_Mp3Decoder.m_snd_file->buffer) + m_Mp3Decoder.m_snd_file->offset;

            for( ; idx < total_samples && m_Mp3Decoder.m_snd_file->offset < m_Mp3Decoder.m_snd_file->buffer_size / sizeof(short); m_Mp3Decoder.m_snd_file->left_samples--, m_Mp3Decoder.m_snd_file->offset++, dest++, src++, idx++ )
            {
                *dest = *src;
            }
        }

        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %i samples", idx);

    }

    if (err == MPG123_DONE)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %i samples", idx);
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %u bytes", idx * sizeof(short));

        m_Mp3Decoder.cleanup();

        return idx;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: decoding failed");

        m_Mp3Decoder.cleanup();

        return 0;
    }
}

/**
 *
 */
JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_delete(JNIEnv *env, jobject self, jlong sound_file_handle)
{
    m_Mp3Decoder.cleanup();
}