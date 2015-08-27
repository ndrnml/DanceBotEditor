# Tiny GNU Makefile fragment
# Group your sources into “modules”. like static library, shared library, standalone executable
#
# !For LOCAL_SRC_FILES only list source files!

#
# DanceBot native source files
#
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := NativeSoundHandler

LOCAL_SRC_FILES := NativeSoundHandler.cpp \
                   SoundFile.cpp \
                   Mp3Decoder.cpp \
                   BeatExtractor.cpp

LOCAL_SHARED_LIBRARIES := mpg123

LOCAL_STATIC_LIBRARIES := vamp qm

LOCAL_LDLIBS := -llog -landroid
LOCAL_CFLAGS = -O3

include $(BUILD_SHARED_LIBRARY)

ZPATH := $(LOCAL_PATH)

include $(ZPATH)/mpg123/Android.mk
include $(ZPATH)/vamp/Android.mk
include $(ZPATH)/qm-dsp/Android.mk
include $(ZPATH)/qm-vamp/Android.mk