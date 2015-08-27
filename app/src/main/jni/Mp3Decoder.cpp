#include <stdlib.h>
#include <android/log.h>

#include "Mp3Decoder.h"

static const char* LOG_TAG = "NATIVE_MP3_DECODER";

/**
 * TODO comment
 */
Mp3Decoder::Mp3Decoder() :
    m_mh(NULL)
{}

/**
 * TODO comment
 */
int Mp3Decoder::init(SoundFile* snd_file)
{

    // Initialize mpg123 library
    mpg123_init();

    if (openFile(snd_file) != MPG123_OK)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: could not open music file.");
        cleanup();
        return -1;
    }
}

/**
 * TODO
 */
int Mp3Decoder::openFile(SoundFile* snd_file)
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
    m_mh = mpg123_new(NULL, &err);

    if (err == MPG123_OK && m_mh != NULL)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file path: %s", snd_file->file_path);

        err = mpg123_open(m_mh, snd_file->file_path);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_open err: %i", err);
            cleanup();
            return err;
        }

        err = mpg123_getformat(m_mh, &rate, &channels, &encoding);

        if (err != MPG123_OK)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: mpg123_getformat err: %i", err);
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Trouble with mpg123: %s", mpg123_strerror(m_mh));
            cleanup();
            return err;
        }

        // Reset internal format table and only allow specific encodings
        mpg123_format_none(m_mh);

        // TODO: remove this: Force 32 bit float encoding
        //mp3->encoding = MPG123_ENC_FLOAT_32;
        encoding = MPG123_ENC_SIGNED_16;

        // Set fixed format
        mpg123_format(m_mh, rate, channels, encoding);

        // Store the maximum buffer size that is possible
        // The buffer will be needed in the reading/decoding step
        buffer_size = mpg123_outblock(m_mh);

        // Store number of samples of one channel of current track
        num_samples = mpg123_length(m_mh);

        // TODO: if sound file init fails, what to do?
        // Everything was properly loaded with mpg123. Initialize sound file
        snd_file->init(channels, rate, num_samples, encoding, buffer_size);

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
int Mp3Decoder::decode(SoundFile* snd_file) {

    // Load pcm buffer
    short* dest = snd_file->pcm_buffer;

    int err = MPG123_OK;

    // TODO: casting? int to long
    long total_samples = snd_file->num_samples * snd_file->channels;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "mp3 number of samples: %li", snd_file->num_samples);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "total number of samples to process: %li", total_samples);

    int idx = 0;
    // TODO: For some songs processed number of samples does not match expected number to process
    while (idx != total_samples && err == MPG123_OK)
    {
        if (snd_file->left_samples <= 0)
        {
            size_t done = 0;
            err = mpg123_read( m_mh, snd_file->buffer, snd_file->buffer_size, &done );

            // TODO: this is fixed to short encoding (MPG123_ENC_SIGNED_16)
            snd_file->left_samples = done / sizeof(short);
            snd_file->offset = 0;

        }
        else
        {
            short* src = ((short*)snd_file->buffer) + snd_file->offset;

            for( ; idx < total_samples && snd_file->offset < snd_file->buffer_size / sizeof(short); snd_file->left_samples--, snd_file->offset++, dest++, src++, idx++ )
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

        cleanup();

        return idx;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: decoding failed");

        cleanup();

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