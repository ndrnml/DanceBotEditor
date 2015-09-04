#include <android/log.h>

#include <qm-vamp/BeatTrack.h>

#include "SoundFile.h"
#include "BeatExtractor.h"

static const char* LOG_TAG = "NATIVE_BEAT_EXTRACTOR";

/**
 * TODO
 */
int BeatExtractor::extractBeats(SoundFile* sound_file, int* beat_buffer, int beat_buffer_size)
{
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "start BeatExtractor::extractBeats");

    // Prepare sound file for beat extraction
    sound_file->prepareForBeatExtraction();

    // Get information from sound file
    long num_samples = sound_file->num_samples;
    long sample_rate = sound_file->rate;

    // Initialize beat tracking
    int number_of_beats_detected = 0;

    // TODO: cast sample_rate to float?
    BeatTracker beat_tracker = BeatTracker(sample_rate/*static_cast<float>(sample_rate)*/);

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "identifier: %s", beat_tracker.getIdentifier().c_str());

    // Block size and step size information for the qm-vamp plugin
    int block_size = beat_tracker.getPreferredBlockSize();
    int step_size = beat_tracker.getPreferredStepSize();

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred block size: %i", block_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "preferred step size: %i", step_size);

    // Force mono channel, otherwise it does not work TODO: does it?
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

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "initialised beat tracker");

    // TODO:
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

    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat buffer length: %i", beat_buffer_size);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "number of beats detected: %i", number_of_beats_detected);
    __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "beat extraction finished");

    // Store the total number of beats detected
    m_total_beats_detected = number_of_beats_detected;

    return number_of_beats_detected;
}


/**
 * TODO comment
 */
int BeatExtractor::getNumBeatsDetected()
{
    return m_total_beats_detected;
}