/*
 * The class that handles the logging based on the given platform
 *
 * Author: André Pomp
 *
 */

#ifndef LOGGING_H
#define LOGGING_H

#ifdef __ANDROID__
	#include <android/log.h>
	#define  LOG_TAG "GazeTracker C++"
	#define  LOG(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#elif __linux__
	#include <stdio.h>
	#include <stdlib.h>
	#define LOG(...) fprintf(stderr, __VA_ARGS__)
#endif

#endif
