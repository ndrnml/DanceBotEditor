#
# qm dsp library
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

DSP_CFLAGS := \
    -O3 \
    -fPIC \
    -ffast-math \
    -fomit-frame-pointer \
    -DUSE_PTHREADS

DSP_SRC_FILES := \
    libdsp/base/Pitch.cpp \
    libdsp/dsp/onsets/DetectionFunction.cpp \
    libdsp/dsp/onsets/PeakPicking.cpp \
    libdsp/dsp/phasevocoder/PhaseVocoder.cpp \
    libdsp/dsp/tempotracking/TempoTrack.cpp \
    libdsp/dsp/tempotracking/TempoTrackV2.cpp \
    libdsp/dsp/transforms/FFT.cpp \
    libdsp/dsp/signalconditioning/DFProcess.cpp \
    libdsp/dsp/signalconditioning/Filter.cpp \
    libdsp/dsp/signalconditioning/FiltFilt.cpp \
    libdsp/dsp/signalconditioning/Framer.cpp \
    libdsp/maths/Correlation.cpp \
    libdsp/maths/CosineDistance.cpp \
    libdsp/maths/KLDivergence.cpp \
    libdsp/maths/MathUtilities.cpp \
    libdsp/maths/pca/pca.c \
    libdsp/thread/Thread.cpp

LOCAL_MODULE            := dsp
LOCAL_C_INCLUDES := $(LOCAL_PATH)/libdsp
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libdsp
LOCAL_SRC_FILES         := $(DSP_SRC_FILES)
LOCAL_CFLAGS            := $(DSP_CFLAGS)

include $(BUILD_STATIC_LIBRARY)