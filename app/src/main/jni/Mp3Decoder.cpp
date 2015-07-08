#include <stdlib.h>
#include <android/log.h>

#include <libmpg123/mpg123.h>

#include "Mp3Decoder.h"

static const char* LOG_TAG = "NATIVE_MP3_DECODER";

/**
 * TODO comment
 */
Mp3Decoder::Mp3Decoder() :
    m_sound_file(NULL),
    m_mh(NULL)
{}

/**
 * TODO comment
 */
int Mp3Decoder::init(SoundFile* snd_file)
{

    // Initialize mpg123 library
    mpg123_init();

    // Attach sound file to decoder
    m_sound_file = snd_file;

    if (openFile() != MPG123_OK)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: could not open music file.");
        cleanup();
        return -1;
    }
}

/**
 * TODO
 */
int Mp3Decoder::openFile()
{
    // Error code
    int err = MPG123_OK;

    // Create new mpg123 handle
    m_mh = mpg123_new(NULL, &err);

    if (err == MPG123_OK && m_mh != NULL)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file path: %s", m_sound_file->file_path);

        err = mpg123_open(m_mh, m_sound_file->file_path);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_open err: %i", err);
            cleanup();
            return err;
        }

        err = mpg123_getformat(m_mh, &m_sound_file->rate, &m_sound_file->channels, &m_sound_file->encoding);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "rate: %li", m_sound_file->encoding);
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "channels  %i", m_sound_file->channels);
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "encoding  %i", m_sound_file->encoding);

            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_getformat err: %i", err);
            cleanup();
            return err;
        }

        // Reset internal format table and only allow specific encodings
        mpg123_format_none(m_mh);

        // TODO: remove this: Force 32 bit float encoding
        //mp3->encoding = MPG123_ENC_FLOAT_32;
        m_sound_file->encoding = MPG123_ENC_SIGNED_16;

        mpg123_format(m_mh, m_sound_file->rate, m_sound_file->channels, m_sound_file->encoding);

        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mp3 rate: %li", m_sound_file->rate);

        // Store the maximum buffer size that is possible
        // The buffer will be needed in the reading/decoding step
        m_sound_file->buffer_size = mpg123_outblock(m_mh);
        m_sound_file->buffer = (unsigned char*)malloc(m_sound_file->buffer_size);

        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mp3 buffer size: %i", m_sound_file->buffer_size);

        // Store number of samples of one channel of current track
        m_sound_file->num_samples = mpg123_length(m_mh);
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: no proper initialization of mpg123lib.");
        cleanup();
        return -1;
    }

    return MPG123_OK;
}

/**
 * TODO: The hole method is fixed to short encoding
 */
int Mp3Decoder::decode() {

    // Prepare memory for pcm data
    m_sound_file->pcm_buffer = new short[m_sound_file->num_samples * m_sound_file->channels];

    int err = MPG123_OK;

    // TODO: casting? int to long
    long num_samples = m_sound_file->num_samples * m_sound_file->channels;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mp3 number of samples: %li", m_sound_file->num_samples);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "total number of samples to process: %li", num_samples);

    int idx = 0;
    // TODO: For some songs processed number of samples does not match expected number to process
    while (idx != num_samples && err == MPG123_OK)
    {
        if (m_sound_file->left_samples <= 0)
        {
            size_t done = 0;
            err = mpg123_read( m_mh, m_sound_file->buffer, m_sound_file->buffer_size, &done );

            // TODO: this is fixed to short encoding (MPG123_ENC_SIGNED_16)
            m_sound_file->left_samples = done / sizeof(short);
            m_sound_file->offset = 0;

        }
        else
        {
            short* src = ((short*)m_sound_file->buffer) + m_sound_file->offset;
            for( ; idx < num_samples && m_sound_file->offset < m_sound_file->buffer_size / sizeof(short); m_sound_file->left_samples--, m_sound_file->offset++, m_sound_file->pcm_buffer++, src++, idx++ )
            {
                *(m_sound_file->pcm_buffer) = *src;
            }
        }

    }

    if (err == MPG123_DONE)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %i samples", idx);
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed: %ui bytes", idx * sizeof(short));
        return idx;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: decoding failed");
        return 0;
    }

}

/**
 *
 */
void Mp3Decoder::cleanup()
{
	// It's to late for error checks here
	mpg123_close(m_mh);
	mpg123_delete(m_mh);
	mpg123_exit();
}