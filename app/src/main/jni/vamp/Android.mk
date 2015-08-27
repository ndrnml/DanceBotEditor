#
# vamp plugin library
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

VAMP_SRC_FILES := libvamp/src/vamp-hostsdk/PluginBufferingAdapter.cpp \
		libvamp/src/vamp-hostsdk/PluginChannelAdapter.cpp \
		libvamp/src/vamp-hostsdk/PluginHostAdapter.cpp \
		libvamp/src/vamp-hostsdk/PluginInputDomainAdapter.cpp \
		libvamp/src/vamp-hostsdk/PluginLoader.cpp \
		libvamp/src/vamp-hostsdk/PluginSummarisingAdapter.cpp \
		libvamp/src/vamp-hostsdk/PluginWrapper.cpp \
		libvamp/src/vamp-hostsdk/RealTime.cpp \
		libvamp/src/vamp-sdk/FFT.cpp \
		libvamp/src/vamp-sdk/FFTimpl.cpp \
		libvamp/src/vamp-sdk/PluginAdapter.cpp \
		libvamp/src/vamp-sdk/RealTime.cpp

LOCAL_MODULE            := vamp
LOCAL_C_INCLUDES		:= $(LOCAL_PATH)/libvamp
LOCAL_EXPORT_C_INCLUDES	:= $(LOCAL_PATH)/libvamp
LOCAL_SRC_FILES         := $(VAMP_SRC_FILES)

include $(BUILD_STATIC_LIBRARY)