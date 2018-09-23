#include <android/log.h>

#include <qm-vamp/BeatTrack.h>

#include <exception>

#include "SoundFile.h"
#include "BeatExtractor.h"

static const char* LOG_TAG = "NATIVE_BEAT_EXTRACTOR";

long BeatExtractor::num_processed_samples = 0;

/**
 * This method computes the features (beat peaks) at sample positions for a selected song
 * which soundFileHandle is pointing to
 */
JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_extractBeats
        (JNIEnv *env, jobject self, jlong soundFileHandle, jobject intBuffer, jint intBufferSize)
{
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "start BeatExtractor::extractBeats");

    // Get starting address of int buffer where the extracted beats will be stored
    int* beat_buffer = (int*)env->GetDirectBufferAddress(intBuffer);

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat int buffer assigned");

    // Pass the length of the fixed size beat buffer (int buffer)
    int beat_buffer_size = intBufferSize;

    // TODO Is this safe todo? Do we have to check for null?
    if (soundFileHandle == 0) {
        // If sound file handle is NULL return zero beat detected
        return 0;
    }
    // Cast sound file handle pointer
    SoundFile *sound_file = (SoundFile *) soundFileHandle;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file handle: %p", sound_file);

    // Get information from sound file
    long num_samples = sound_file->num_samples;
    long sample_rate = sound_file->rate;

    // Initialize beat tracking
    int number_of_beats_detected = 0;

    // Vamp beat tracker takes the input sample rate
    // Bar and Beat Tracker analyses a single channel of audio and estimates the positions of bar
    // lines and the resulting counted metrical beat positions within the music
    BeatTracker beat_tracker = BeatTracker(static_cast<float>(sample_rate));

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "identifier: %s", beat_tracker.getIdentifier().c_str());

    // Block size and step size information for the qm-vamp plugin
    int block_size = beat_tracker.getPreferredBlockSize();
    int step_size = beat_tracker.getPreferredStepSize();

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred block size: %i", block_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred step size: %i", step_size);

    // Force mono channel, otherwise it the bar and beat tracker plugin does not work
    int channels = 1;

    // Assign new plugin buffers for qm-vamp TODO: shouldn't they be deleted somewhere?
    float **plugbuf = new float*;
    *plugbuf = new float[block_size + 2]; // TODO: why + 2?

    // Guarantee that the beat grid information is stored in seconds
    Vamp::RealTime adjustment = Vamp::RealTime::zeroTime;

    // Initializes the qm-vamp plugin
    if (!beat_tracker.initialise(channels, step_size, block_size))
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat tracker not initialised");
    }

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialised beat tracker");

    // Iterate over all samples to find beat locations
    for (int i = 0; i < num_samples; i += step_size)
    {

        int count = num_samples - i > block_size ? block_size : num_samples - i;

		//__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "count i=%i: %i", i, count);

        int j = 0;
        while (j < count)
        {

            // TODO: what is this mapping /32768.0f ?
            (*plugbuf)[j] = (static_cast<float>(sound_file->music_buffer[j + i]))/32768.0f;
			++j;

            //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "plugbuf %i: %f", (j+i), (static_cast<float>(sound_file->pcm_buffer[j + i]))/32768.0f);
        }

        // if j is less than the block size fill with zeros (zero padding)
        while (j < block_size)
        {
		    (*plugbuf)[j] = 0.0f;
            ++j;
        }

        // TODO: Compute the what?
		Vamp::RealTime rt = Vamp::RealTime::frame2RealTime(i, sample_rate);
		int frame = Vamp::RealTime::realTime2Frame(rt + adjustment, sample_rate);

        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "frame: %i", frame);

        // Initialize data structure where beat peaks (features) will be stored
		Vamp::Plugin::FeatureSet features = beat_tracker.process(plugbuf, rt);

		for (int k = 0; k < features[0].size(); ++k)
		{
            int display_frame = frame;

            if (features[0][k].hasTimestamp)
            {
                display_frame = Vamp::RealTime::realTime2Frame
                    (features[0][k].timestamp, sample_rate);
            }

            //m_beat_buffer.push_back(display_frame);
            if (number_of_beats_detected < beat_buffer_size)
            {
                beat_buffer[number_of_beats_detected] = display_frame;
                number_of_beats_detected += 1;
            }
            //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %p", beat_buffer);
            //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %i", display_frame);
            //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %i", beat_buffer[number_of_beats_detected-1]);
		}

        if ((i % 200000) == 0) {
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed samples: %d", i);
        }
	}

    Vamp::Plugin::FeatureSet features = beat_tracker.getRemainingFeatures();

    Vamp::RealTime rt = Vamp::RealTime::frame2RealTime(num_samples, sample_rate);
    int frame = Vamp::RealTime::realTime2Frame(rt + adjustment, sample_rate);

    for (int k = 0; k < features[0].size(); ++k)
    {
        int display_frame = frame;

        if (features[0][k].hasTimestamp) {
            display_frame = Vamp::RealTime::realTime2Frame
                (features[0][k].timestamp, sample_rate);
        }

        //m_beat_buffer.push_back(display_frame);
        if (number_of_beats_detected < beat_buffer_size)
        {
            beat_buffer[number_of_beats_detected] = display_frame;
            number_of_beats_detected += 1;
        }
        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %p", beat_buffer);
        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %i", display_frame);
        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat at: %i", beat_buffer[number_of_beats_detected-1]);
    }

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat buffer length: %i", beat_buffer_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "number of beats detected: %i", number_of_beats_detected);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat extraction finished");

    // Store the total number of beats detected in sound file
    sound_file->number_beats_detected = number_of_beats_detected;

    return number_of_beats_detected;
}

/**
 * THIS METHOD IS NOT THREAD SAFE
 * This method computes the features (beat peaks) at sample positions for a given range of samples
 */
JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_extract(
        JNIEnv *env,
        jobject self,
        jlong soundFileHandle,
        jobject intBuffer,
        jint intBufferSize,
        jlong startSample,
        jlong endSample)
{

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "start BeatExtractor::extractBeats");

    // Get starting address of int buffer where the extracted beats will be stored
    int* beat_buffer = (int*)env->GetDirectBufferAddress(intBuffer);

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat int buffer assigned");

    // Pass the length of the fixed size beat buffer (int buffer)
    int beat_buffer_size = intBufferSize;

    // TODO Is this safe todo? Do we have to check for null?
    if (soundFileHandle == 0) {
        // If sound file handle is NULL return zero beat detected
        return 0;
    }
    // Cast sound file handle pointer
    SoundFile *sound_file = (SoundFile *) soundFileHandle;

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Sound file handle: %p", sound_file);

    // Get information from sound file
    long num_samples = sound_file->num_samples;
    long sample_rate = sound_file->rate;

    // Number of beats detected initially set to zero
    int number_of_beats_detected = 0;


    /* Vamp beat tracker takes the input sample rate
     * Bar and Beat Tracker analyses a single channel of audio and estimates the positions of bar
     * lines and the resulting counted metrical beat positions within the music
     */
    BeatTracker beat_tracker = BeatTracker(static_cast<float>(sample_rate));

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "identifier: %s",
                        beat_tracker.getIdentifier().c_str());

    // Block size and step size information for the qm-vamp plugin
    int block_size = beat_tracker.getPreferredBlockSize();
    int step_size = beat_tracker.getPreferredStepSize();

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred block size: %i", block_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred step size: %i", step_size);

    // Force mono channel, otherwise it the bar and beat tracker plugin does not work
    int channels = 1;

    // Assign new plugin buffers for qm-vamp TODO: shouldn't they be deleted somewhere?
    float **plugbuf = new float *;
    *plugbuf = new float[block_size + 2];

    // Guarantee that the beat grid information is stored in seconds
    Vamp::RealTime adjustment = Vamp::RealTime::zeroTime;

    // Initializes the qm-vamp plugin
    if (!beat_tracker.initialise(channels, step_size, block_size)) {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat tracker not initialised");
    }

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialised beat tracker");
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat extraction from: %i to: %i",
                        (int) startSample, (int) endSample);

    // TODO: Throw and handle exception
    // Iterate over all samples to find beat locations
    for (int i = startSample; i < endSample; i += step_size) {

        int count = num_samples - i > block_size ? block_size : num_samples - i;

        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "count i=%i: %i", i, count);

        int j = 0;
        while (j < count) {

            // TODO: what is this mapping /32768.0f ?
            (*plugbuf)[j] = (static_cast<float>(sound_file->music_buffer[j + i])) / 32768.0f;
            ++j;
        }

        // if j is less than the block size fill with zeros (zero padding)
        while (j < block_size) {
            (*plugbuf)[j] = 0.0f;
            ++j;
        }

        /*
         * According to the sample rate map features (beat peaks) to real time stamps (samples)
         */
        Vamp::RealTime rt = Vamp::RealTime::frame2RealTime(i, sample_rate);
        int frame = Vamp::RealTime::realTime2Frame(rt + adjustment, sample_rate);

        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "frame: %i", frame);

        // Initialize data structure where beat peaks (features) will be stored
        Vamp::Plugin::FeatureSet features = beat_tracker.process(plugbuf, rt);

        /*
        for (int k = 0; k < features[0].size(); ++k) {
            int display_frame = frame;

            if (features[0][k].hasTimestamp) {
                display_frame = Vamp::RealTime::realTime2Frame
                        (features[0][k].timestamp, sample_rate);
            }

            if (number_of_beats_detected < beat_buffer_size) {

                __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "IS THIS EVER CALLED?");

                beat_buffer[number_of_beats_detected] = display_frame;

                // Set number of beats detected
                number_of_beats_detected += 1;

                /*
                 * THIS IS NOT THREAD SAFE
                 * Write current number of detected beats to the sound file to keep track
                 * of progress
                 */
                /*sound_file->number_beats_detected += 1;
            }
        }*/

        // THIS IS NOT THREAD SAFE
        // It is possible to make a callback to the UI thread, so that the progress can be properly
        // updated
        BeatExtractor::num_processed_samples += step_size;
        //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "processed samples: %d", sound_file->num_of_proc_beat_extract_samples);
    }

    Vamp::Plugin::FeatureSet features = beat_tracker.getRemainingFeatures();

    Vamp::RealTime rt = Vamp::RealTime::frame2RealTime(num_samples, sample_rate);
    int frame = Vamp::RealTime::realTime2Frame(rt + adjustment, sample_rate);

    /*
     * Process all found features (beat peaks) and store time stamps to beat buffer
     */
    for (int k = 0; k < features[0].size(); ++k) {
        int display_frame = frame;

        if (features[0][k].hasTimestamp) {
            display_frame = Vamp::RealTime::realTime2Frame
                    (features[0][k].timestamp, sample_rate);
        }

        //m_beat_buffer.push_back(display_frame);
        if (number_of_beats_detected < beat_buffer_size) {

            beat_buffer[number_of_beats_detected] = display_frame;

            // Set number of beats detected
            number_of_beats_detected += 1;

            /*
             * THIS IS NOT THREAD SAFE
             * Write current number of detected beats to the sound file to keep track
             * of progress
             */
            sound_file->number_beats_detected += 1;
            //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "sound_file->number_beats_detected: %i", number_of_beats_detected);
        }
    }

    //__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat buffer length: %i", beat_buffer_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "number of beats detected: %i",
                        number_of_beats_detected);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat extraction finished");

    // THIS IS ALSO NOT THREAD SAFE!
    // Store the total number of beats detected in sound file
    //sound_file->number_beats_detected = number_of_beats_detected;

    return number_of_beats_detected;
}

JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_cleanUp
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "cleanUp(): sound_file_handle: %lld", sound_file_handle);
    if (sound_file_handle != 0)
    {
        SoundFile *sound_file = (SoundFile *)sound_file_handle;
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "cleanUp(): hugo: %d", sound_file->channels);
        delete sound_file;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "Error: sound file handle clean up failed, sound_file_handle: %lld", sound_file_handle);
    }
}

JNIEXPORT jlong JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_getProcessedSamples
        (JNIEnv*, jobject)
{
    return BeatExtractor::num_processed_samples;
}


JNIEXPORT jint JNICALL Java_org_vamp_beatextraction_VampBeatExtractor_getNumBeatsDetected
        (JNIEnv *env, jobject self, jlong sound_file_handle)
{
    SoundFile *sound_file = (SoundFile *)sound_file_handle;

    return sound_file->number_beats_detected;
}