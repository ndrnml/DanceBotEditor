# Tiny GNU Makefile fragment
# Group your sources into “modules”. like static library, shared library, standalone executable
#
# !For LOCAL_SRC_FILES only list source files!

LOCAL_PATH := $(call my-dir)


#
# mpg123 static library
#
include $(CLEAR_VARS)
LOCAL_MODULE := libmpg123
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libmpg123
LOCAL_SRC_FILES := libmpg123/$(TARGET_ARCH_ABI)/libmpg123.a
include $(PREBUILT_STATIC_LIBRARY)


#
# DanceBot native source files
#
include $(CLEAR_VARS)
LOCAL_MODULE := NativeSoundHandler

LOCAL_SRC_FILES := NativeSoundHandler.cpp \
                   SoundFile.cpp \
                   Mp3Decoder.cpp

LOCAL_STATIC_LIBRARIES := libmpg123

LOCAL_LDLIBS := -llog -landroid
LOCAL_CFLAGS = -O3
include $(BUILD_SHARED_LIBRARY)