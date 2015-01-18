The purpose of this file is to describe which values are recorded during the test. 

Each file has the filename:
testtype_MM_currenttime.json

testtype: point test
MM:  The abbreviation of the test subject that was entered at the beginning (e.g., AP)
currenttime: The time when the test finished

Each test file consists of several parts as is recorded using JSON format:
For each test sequence one file is recorded.

1. General Information:
General Information parts begin with a GENERAL in the key name.
These are information which are important for each test (independent of the test type).
They include:
    "GENERAL_DEVICE_MODEL"        : The device model on which the test was done
    "GENERAL_DEVICE_ORIENTATION"  : The device orientation as string ( (reverse) portrait or (reverse) landscape)
    "GENERAL_SCREEN_WIDTH",       : The screen width of the test
    "GENERAL_SCREEN_HEIGHT"       : The screen height of the test
    "GENERAL_START_TIME"          : The start time when the test was started
    "GENERAL_ABBREVIATION"        : The abbreviation of the test subject
    "GENERAL_TEST_TYPE"           : The test type
    
2. Test Specific Information
These information depend on the test and also on the current test settings. 
They can change among different tests (even with the same test subject).
They include:
    "POINT_NUMBER_OF_REPETITIONS"       : Indicates the number of repetitions for each point
    "POINT_TEST_TIME_PER_POINT"         : Indicates the time for which each point was displayed
    "POINT_TEST_NUMBER_OF_COLS"         : Indicates the number of columns of points
    "POINT_TEST_NUMBER_OF_ROWS"         : Indicates the number of rows of points
    
3. Line Specific Information
These are information for each single point that is displayed. This is a JSON array consisting of the different points
that were displayed. Each point is a single JSON Object.
	"POINT_CENTER_X"        : the x coordinate of this point
	"POINT_CENTER_Y"        : the y coordinate of this point
	"POINT_START_TIME"      : The time when the point was displayed