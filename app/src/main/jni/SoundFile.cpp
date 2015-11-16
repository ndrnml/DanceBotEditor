#include <stdlib.h>
#include <android/log.h>

#include "SoundFile.h"

static const char* LOG_TAG = "NATIVE_SOUND_FILE";

SoundFile::SoundFile(const char* file_path_) :
    file_path(file_path_),
    number_beats_detected(0),
    channels(0),
    rate(0),
    num_samples(0),
    encoding(0),
    buffer(NULL),
    left_samples(0),
    offset(0),
    pcm_buffer(NULL),
    music_buffer(NULL)
{}

SoundFile::~SoundFile()
{
     free(buffer);
     delete[] pcm_buffer;
     delete[] music_buffer;
}

int SoundFile::init(int channels_, long rate_, long num_samples_, int encoding_, size_t buffer_size_)
{
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialize sound file...");

    // Basic sound file information
    channels = channels_;
    rate = rate_;
    num_samples = num_samples_;
    encoding = encoding_;
    buffer_size = buffer_size_;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Format rate: %li", rate);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Format channels  %i", channels);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Format encoding  %i", encoding);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Format buffer size: %i", buffer_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Format number of samples per channel: %li", num_samples);

    // The buffer will be needed in the reading/decoding step
    buffer = (unsigned char*)malloc(buffer_size);

    // Prepare memory for pcm data
    pcm_buffer = new short[num_samples * channels];
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "assigned pcm buffer, size: %li", num_samples * channels);

    // TODO: check if channel is really == 2
    // Interpolate raw (pcm) audio data
    music_buffer = new short[num_samples];
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "assigned music buffer, size: %li", num_samples);

    return 0;
}

int SoundFile::interleaveChannels()
{

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "music_buffer: %p", music_buffer);
    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "channels: %d", channels);

    // TODO: What if channels == 1?
    // Check if music buffer was initialized properly
    if (music_buffer != NULL && channels == 2)
    {
        for (int i = 0; i < num_samples; ++i)
        {
            // TODO: is this possible with short?
            music_buffer[i] = (pcm_buffer[2*i] + pcm_buffer[2*i + 1]) / 2;

            /*if (i % 200000 == 0) {
                __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "sample: %d", i);
            }*/
        }

        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "interpolated left and right pcm channels");

        return 0;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "something went wrong with the interpolation");

        return -1;
    }

}

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_getSampleRate(JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->rate;
}

JNIEXPORT jlong JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_Decoder_getNumberOfSamples(JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->num_samples;
}

JNIEXPORT jint JNICALL Java_ch_ethz_asl_dancebots_danceboteditor_utils_BeatExtractor_getNumBeatsDetected(JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->number_beats_detected;
}