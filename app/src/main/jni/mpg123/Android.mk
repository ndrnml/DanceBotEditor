#
# mpg123 static library
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

MPG123_CFLAGS := \
	-DACCURATE_ROUNDING \
	-DREAL_IS_FIXED \
	-DNO_REAL \
	-DNO_32BIT

MPG123_SRC_FILES := \
	libmpg123/compat.c \
	libmpg123/frame.c \
	libmpg123/id3.c \
	libmpg123/format.c \
	libmpg123/stringbuf.c \
	libmpg123/libmpg123.c\
	libmpg123/readers.c\
	libmpg123/icy.c\
	libmpg123/icy2utf8.c\
	libmpg123/index.c\
	libmpg123/layer1.c\
	libmpg123/layer2.c\
	libmpg123/layer3.c\
	libmpg123/parse.c\
	libmpg123/optimize.c\
	libmpg123/synth.c\
	libmpg123/synth_8bit.c\
	libmpg123/ntom.c\
	libmpg123/dct64.c\
	libmpg123/dct64_i386.c \
	libmpg123/equalizer.c\
	libmpg123/dither.c\
	libmpg123/tabinit.c\
	libmpg123/feature.c

ifeq ($(TARGET_ARCH),x86)
		MPG123_CFLAGS += -DOPT_I386
else
		LOCAL_ARM_MODE := arm
		MPG123_CFLAGS += -DOPT_ARM
		MPG123_SRC_FILES += libmpg123/synth_arm_accurate.S \
			libmpg123/synth_arm.S
endif

LOCAL_MODULE    		:= mpg123
LOCAL_SRC_FILES 		:= $(MPG123_SRC_FILES)
LOCAL_CFLAGS    		:= $(MPG123_CFLAGS)
LOCAL_LDLIBS    		:= -llog

include $(BUILD_SHARED_LIBRARY)