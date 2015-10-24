#ifndef MP3_DECODER_H_
#define MP3_DECODER_H_

#include <mpg123/mpg123.h>

#include "SoundFile.h"

class Mp3Decoder {

public:

    Mp3Decoder();
    ~Mp3Decoder() {};

    int init(SoundFile* snd_file);
    int decode();

private:

    SoundFile* m_snd_file;

    int openFile();
    void cleanup();

    mpg123_handle* m_mh;
};

#endif