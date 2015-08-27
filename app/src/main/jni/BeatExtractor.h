#ifndef BEAT_EXTRACTOR_H_
#define BEAT_EXTRACTOR_H_

class BeatExtractor {

public:

    int extractBeats(SoundFile* sound_file, int* beat_buffer, int beat_buffer_size);

};

#endif