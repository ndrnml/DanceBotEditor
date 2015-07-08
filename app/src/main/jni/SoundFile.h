#ifndef SOUND_FILE_H_
#define SOUND_FILE_H_

#include <stdlib.h>

// TODO: replace by SNDFILE
class SoundFile {

public:

    SoundFile();
    ~SoundFile();

    const char* file_path;
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
};

#endif