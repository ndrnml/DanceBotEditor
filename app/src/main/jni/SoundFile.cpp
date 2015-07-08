#include <stdlib.h>
#include <android/log.h>

#include "SoundFile.h"

static const char* LOG_TAG = "NATIVE_SOUND_FILE";

SoundFile::SoundFile() :
    file_path(NULL),
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