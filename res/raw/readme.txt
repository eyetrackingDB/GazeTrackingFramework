The purpose of this file is to describe which values are recorded.

1. video_recording.mp4 : 
The video recording of the front camera.

2. screen_recording.mp4 :
The screen_recording of the device. This could either be a test_view or the live_screen.

3. sensor_acc.log
The data of the accelerometer sensor measured in m/s2 for x,y,z axis. All values include the force of gravity. 
For more information, see http://developer.android.com/guide/topics/sensors/sensors_overview.html
Col 1: timestamp
Col 2: acceleration around x-axis
Col 3: acceleration around y-axis
Col 4: acceleration around z-axis

4. sensor_gyro.log
The data of the gyroscope sensor measured in rad/s for x,y,z axis. This is the rate of rotation for each axis. 
For more information, see http://developer.android.com/guide/topics/sensors/sensors_overview.html
Col 1: timestamp
Col 2: rate of rotation for x-axis
Col 3: rate of rotation for y-axis
Col 4: rate of rotation for z-axis

5. sensor_light.log
The data of the light sensor measured in lx.  This is the ambient light level (illumination). 
For more information, see http://developer.android.com/guide/topics/sensors/sensors_overview.html
Col 1: timestamp
Col 2: illumination
Col 3: not relevant
Col 4: not relevant

6. sensor_rot.log
Instead of providing the raw data of the rotation sensor, we here calculate the rotation of the device
as azimuth, pitch and roll measured in degrees.
For more information, see http://developer.android.com/guide/topics/sensors/sensors_overview.html
Col 1: timestamp
Col 2: azimuth, rotation around the Z axis
Col 3: pitch, rotation around the X axis
Col 4: roll, rotation around the Y axis

7. GazeTrackingSettings.txt
This file contains the settings for the post processing of the recorded video file. It contains values that 
are device specific and that are required for post processing the video. If you process the video on the device
the file is read in before post processing. If you process the video somewhere else this file is required to accurately
post process the video. It contains the following content: 
DEVICE_MODEL         : The Device Model on which the test was made
ORIENTATION          : The orientation of the device during the test => the orientation is not allowed to change!
SCREEN_WIDTH_PX      : The screen width in px of the screen (dependent on the orientation that was used during the test)
SCREEN_HEIGHT_PX     : The screen height in px of the screen (dependent on the orientation that was used during the test)
SCREEN_WIDTH_MM      : The screen width in mm of the screen (dependent on the orientation that was used during the test)
SCREEN_HEIGHT_MM     : The screen height in mm of the screen (dependent on the orientation that was used during the test)
CAMERA_RES_WIDTH     : The camera res width in px the recorded video (dependent on the orientation that was used during the test)
CAMERA_RES_HEIGHT    : The camera res height in px the recorded video (dependent on the orientation that was used during the test)
CAMERA_OFFSET_X      : The offset for the x-axis of the camera position (dependent on the orientation that was used during the test)
CAMERA_OFFSET_Y      : The offset for the y-axis of the camera position (dependent on the orientation that was used during the test)
CAMERA_PRIN_POINT_X  : The principal point of the camera x-axis that is determined via camera calibration (dependent on the orientation that was used during the test) 
CAMERA_PRIN_POINT_Y  : The principal point of the camera y-axis that is determined via camera calibration (dependent on the orientation that was used during the test) 
CAMERA_FOCAL_LEN_X   : The focal length of the camera x-axis that is determined via camera calibration (dependent on the orientation that was used during the test) 
CAMERA_FOCAL_LEN_Y   : The focal length of the camera y-axis that is determined via camera calibration (dependent on the orientation that was used during the test) 
CAMERA_DISTORT_0     : Value 0 of the camera distortion matrix that is determined via the camera calibration (dependent on the orientation that was used during the test) 
CAMERA_DISTORT_1     : Value 1 of the camera distortion matrix that is determined via the camera calibration (dependent on the orientation that was used during the test)
CAMERA_DISTORT_2     : Value 2 of the camera distortion matrix that is determined via the camera calibration (dependent on the orientation that was used during the test)
CAMERA_DISTORT_3     : Value 3 of the camera distortion matrix that is determined via the camera calibration (dependent on the orientation that was used during the test)
CAMERA_DISTORT_4     : Value 4 of the camera distortion matrix that is determined via the camera calibration (dependent on the orientation that was used during the test)