#ifndef SOUND_FILE_H_
#define SOUND_FILE_H_

#include <jni.h>
#include <stdlib.h>

// TODO: replace by SNDFILE
/**
 * This class is NOT Thread safe
 */
class SoundFile {

public:

    // Other fields
    int channels;
    long rate;
    long num_samples;
    int encoding;
    size_t buffer_size;
    unsigned char* buffer;
    size_t left_samples;
    size_t offset;
    short* pcm_buffer;
    short* music_buffer;

    // Constructor / destructor
    SoundFile(const char* file_path_);
    ~SoundFile();

    int init(int channels_, long rate_, long num_samples_, int encoding_, size_t buffer_size_);
    int interleaveChannels();

    // Absolute path to the sound file
    const char* file_path;

    // The number of beats detected by one or multiple threads
    int number_beats_detected;
};

#endif