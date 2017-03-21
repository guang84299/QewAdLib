LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:=uninstall
LOCAL_SRC_FILES:=uninstall.c

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/include
LOCAL_SHARED_LIBRARIES := liblog libcutils
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)