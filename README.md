# GazeTrackingFramework

Copyright (C) 2014-2015, André Pomp <andre.pomp@rwth-aachen.de>

Copyright (C) 2014-2015, Jó Ágila Bitsch <jo.bitsch@comsys.rwth-aachen.de>

Copyright (C) 2014-2015, Oliver Hohlfeld <oliver.hohlfeld@comsys.rwth-aachen.de>

Copyright (C) 2014-2015, Chair of Computer Science 4, RWTH Aachen University, <klaus@comsys.rwth-aachen.de>

#### FREE SOFTWARE

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

#### IF YOU NEED ANOTHER LICENSE

If you are planning to integrate NormMaker into a commercial product, please contact us for licensing options via email at:

  jo.bitsch@comsys.rwth-aachen.de
  
#### Requirements
 This project requires:
  * Eclipse with CDT  [click](https://eclipse.org/)
  * Android SDK and NDK [click](http://developer.android.com/sdk/index.html)
  * OpenCV 4 Android [click](http://opencv.org/platforms/android.html)
  * TBB Library [click](https://www.threadingbuildingblocks.org/)
   
  All other libraries are included.
 
#### Used Libraries
 This project bases on different OpenSource libraries. Please respect their license if you use it. You can find copies of their licenses in the "licenses" folder. 
  * EyeTab GazeTracking  [click](https://github.com/errollw/EyeTab/)
  * EyeLike Pupil Detection [click](https://github.com/trishume/eyeLike)
  * OpenCV [click](http://opencv.org/) 
  * JavaCV [click](https://github.com/bytedeco/javacv) 
  * TBB Library [click](https://www.threadingbuildingblocks.org/)
  * Eigen [click](http://eigen.tuxfamily.org/index.php?title=Main_Page)
  * FFMPEG [click](https://www.ffmpeg.org/)
  * FFMPEG 4 Android [click](https://github.com/guardianproject/android-ffmpeg-java)
  * MP4 Parser [click](https://github.com/sannies/mp4parser)
  * RootTools [click](https://github.com/Stericson/RootTools)

#### HOW TO USE THIS SOFTWARE
* Download the tools that are listed under requirements and install them
* Clone the git repository
* Important the project into eclipse
* Go to Properties/Android and set the path to the OpenCV 4 Android library
* Go to the jni/Android.mk file and set the paths for the variables OPEN_CV_FULL_PATH and TBB_FULL_PATH
* Go to Properties/ C/C++ General/ Paths and Symbols and modify the include paths such that they map your folder structure 
* Use the framework 
