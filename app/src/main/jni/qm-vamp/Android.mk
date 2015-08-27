#
# qm vamp plugin
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

QMVAMP_CFLAGS := -O3 \
    -fPIC \
    -ffast-math \
    -fomit-frame-pointer \
    -DUSE_PTHREADS

QMVAMP_SRC_FILES := \
    BeatTrack.cpp \
    libmain.cpp

LOCAL_MODULE            := qm
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/plugins
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/plugins
LOCAL_SRC_FILES         := $(QMVAMP_SRC_FILES)
LOCAL_STATIC_LIBRARIES  := dsp vamp
LOCAL_CFLAGS            := $(QMVAMP_CFLAGS)

include $(BUILD_STATIC_LIBRARY)