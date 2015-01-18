# Author: André Pomp

#General Paths
OPEN_CV_FULL_PATH := D:/OpenCV4Android
TBB_FULL_PATH := D:/TBB

#Set the local path to the JNI folder
LOCAL_PATH := $(call my-dir)

# INCLUDE OPENCV AND OWN MODULE
include $(CLEAR_VARS)

include $(OPEN_CV_FULL_PATH)/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := eyetab_tracker
LOCAL_SRC_FILES := 	eyetab_tracker.cpp \
					EyeTabTracker/GazeSettingsEyeTab.cpp \
					EyeTabTracker/GazeTrackerEyeTab.cpp \
					EyeTabTracker/src_eyetab/erase_specular.cpp \
					EyeTabTracker/src_eyetab/eye_center_isophotes.cpp \
					EyeTabTracker/src_eyetab/eye_center_combined.cpp \
					EyeTabTracker/src_eyetab/fit_ellipse.cpp \
					EyeTabTracker/src_eyetab/utils.cpp \
					EyeTabTracker/src_eyetab/gaze_geometry.cpp \
					EyeTabTracker/src_eyetab/gaze_smoothing.cpp \
					EyeTabTracker/src_eyetab/get_poss_limb_pts.cpp \
					EyeTabTracker/src_eyetab/get_eyelids.cpp \
					EyeTabTracker/src_eyetab/gaze_system.cpp \
					EyeTabTracker/src_eyetab/eye_detect.cpp \
					EyeTabTracker/src_eyelike/findEyeCenter.cpp \
					EyeTabTracker/src_eyelike/helpers.cpp 					
LOCAL_LDLIBS    += -lm -llog -landroid
LOCAL_CFLAGS += -std=c++11 -I$(TBB_FULL_PATH)/include 
include $(BUILD_SHARED_LIBRARY)

# Add prebuilt libavcodec
include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := prebuilt/libavcodec.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libavdevice
include $(CLEAR_VARS)
LOCAL_MODULE := libavdevice
LOCAL_SRC_FILES := prebuilt/libavdevice.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libavfilter
include $(CLEAR_VARS)
LOCAL_MODULE := libavfilter
LOCAL_SRC_FILES := prebuilt/libavfilter.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libavformat
include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := prebuilt/libavformat.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libavutil
include $(CLEAR_VARS)
LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := prebuilt/libavutil.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libffmpeg
include $(CLEAR_VARS)
LOCAL_MODULE := libffmpeg
LOCAL_SRC_FILES := prebuilt/libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniavcodec
include $(CLEAR_VARS)
LOCAL_MODULE := libjniavcodec
LOCAL_SRC_FILES := prebuilt/libjniavcodec.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniavdevice
include $(CLEAR_VARS)
LOCAL_MODULE := libjniavdevice
LOCAL_SRC_FILES := prebuilt/libjniavdevice.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniavfilter
include $(CLEAR_VARS)
LOCAL_MODULE := libjniavfilter
LOCAL_SRC_FILES := prebuilt/libjniavfilter.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniavformat
include $(CLEAR_VARS)
LOCAL_MODULE := libjniavformat
LOCAL_SRC_FILES := prebuilt/libjniavformat.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniavutil
include $(CLEAR_VARS)
LOCAL_MODULE := libjniavutil
LOCAL_SRC_FILES := prebuilt/libjniavutil.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjnicvkernels
include $(CLEAR_VARS)
LOCAL_MODULE := libjnicvkernels
LOCAL_SRC_FILES := prebuilt/libjnicvkernels.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_calib3d
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_calib3d
LOCAL_SRC_FILES := prebuilt/libjniopencv_calib3d.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_contrib
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_contrib
LOCAL_SRC_FILES := prebuilt/libjniopencv_contrib.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_core
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_core
LOCAL_SRC_FILES := prebuilt/libjniopencv_core.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_features2d
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_features2d
LOCAL_SRC_FILES := prebuilt/libjniopencv_features2d.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_flann
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_flann
LOCAL_SRC_FILES := prebuilt/libjniopencv_flann.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_highgui
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_highgui
LOCAL_SRC_FILES := prebuilt/libjniopencv_highgui.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_imgproc
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_imgproc
LOCAL_SRC_FILES := prebuilt/libjniopencv_imgproc.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_legacy
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_legacy
LOCAL_SRC_FILES := prebuilt/libjniopencv_legacy.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_ml
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_ml
LOCAL_SRC_FILES := prebuilt/libjniopencv_ml.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_nonfree
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_nonfree
LOCAL_SRC_FILES := prebuilt/libjniopencv_nonfree.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_objdetect
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_objdetect
LOCAL_SRC_FILES := prebuilt/libjniopencv_objdetect.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_photo
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_photo
LOCAL_SRC_FILES := prebuilt/libjniopencv_photo.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_stitching
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_stitching
LOCAL_SRC_FILES := prebuilt/libjniopencv_stitching.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_superres
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_superres
LOCAL_SRC_FILES := prebuilt/libjniopencv_superres.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_video
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_video
LOCAL_SRC_FILES := prebuilt/libjniopencv_video.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniopencv_videostab
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_videostab
LOCAL_SRC_FILES := prebuilt/libjniopencv_videostab.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjnipostproc
include $(CLEAR_VARS)
LOCAL_MODULE := libjnipostproc
LOCAL_SRC_FILES := prebuilt/libjnipostproc.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniswresample
include $(CLEAR_VARS)
LOCAL_MODULE := libjniswresample
LOCAL_SRC_FILES := prebuilt/libjniswresample.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libjniswscale
include $(CLEAR_VARS)
LOCAL_MODULE := libjniswscale
LOCAL_SRC_FILES := prebuilt/libjniswscale.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_calib3d
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_calib3d
LOCAL_SRC_FILES := prebuilt/libopencv_calib3d.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_contrib
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_contrib
LOCAL_SRC_FILES := prebuilt/libopencv_contrib.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_core
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_core
LOCAL_SRC_FILES := prebuilt/libopencv_core.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_features2d
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_features2d
LOCAL_SRC_FILES := prebuilt/libopencv_features2d.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_flann
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_flann
LOCAL_SRC_FILES := prebuilt/libopencv_flann.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_gpu
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_gpu
LOCAL_SRC_FILES := prebuilt/libopencv_gpu.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_highgui
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_highgui
LOCAL_SRC_FILES := prebuilt/libopencv_highgui.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_imgproc
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_imgproc
LOCAL_SRC_FILES := prebuilt/libopencv_imgproc.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_legacy
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_legacy
LOCAL_SRC_FILES := prebuilt/libopencv_legacy.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_ml
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_ml
LOCAL_SRC_FILES := prebuilt/libopencv_ml.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_nonfree
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_nonfree
LOCAL_SRC_FILES := prebuilt/libopencv_nonfree.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_objdetect
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_objdetect
LOCAL_SRC_FILES := prebuilt/libopencv_objdetect.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_photo
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_photo
LOCAL_SRC_FILES := prebuilt/libopencv_photo.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_stitching
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_stitching
LOCAL_SRC_FILES := prebuilt/libopencv_stitching.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_superres
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_superres
LOCAL_SRC_FILES := prebuilt/libopencv_superres.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_video
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_video
LOCAL_SRC_FILES := prebuilt/libopencv_video.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libopencv_videostab
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_videostab
LOCAL_SRC_FILES := prebuilt/libopencv_videostab.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libpostproc
include $(CLEAR_VARS)
LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := prebuilt/libpostproc.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libswresample
include $(CLEAR_VARS)
LOCAL_MODULE := libswresample
LOCAL_SRC_FILES := prebuilt/libswresample.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libswscale
include $(CLEAR_VARS)
LOCAL_MODULE := libswscale
LOCAL_SRC_FILES := prebuilt/libswscale.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libtbb
include $(CLEAR_VARS)
LOCAL_MODULE := libtbb
LOCAL_SRC_FILES := prebuilt/libtbb.so
include $(PREBUILT_SHARED_LIBRARY)
