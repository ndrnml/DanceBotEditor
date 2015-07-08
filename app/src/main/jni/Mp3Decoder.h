#ifndef MP3_DECODER_H_
#define MP3_DECODER_H_

#include <libmpg123/mpg123.h>

#include "SoundFile.h"

class Mp3Decoder {

public:

    Mp3Decoder();
    ~Mp3Decoder() {};

    int init(SoundFile* snd_file);
    int decode();

private:

    int openFile();
    void cleanup();

    SoundFile* m_sound_file;
    mpg123_handle* m_mh;
};

#endif