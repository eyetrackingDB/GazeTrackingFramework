The purpose of this file is to describe which values are recorded during the test. 

Each file has the filename:
testtype_XxY_MM_currenttime.json

testtype: rectangle_single_test or rectangle_sequence_test (depends on which test was chosen)
XxY: The type of sequence that was recorded (e.g., 1x2, 2x2 ...)
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
    "RECTANGLE_TEST_PLAY_TONE": true,                : indicates if a tone was played before the rectangle was shown
    "RECTANGLE_TEST_PLAY_MUSIC": true,               : indicates if music was played during the pause (if pause is 0 no music is played at all)
    "RECTANGLE_TEST_TIME_PER_RECTANGLE"              : the time for which a rectangle is shown
    "RECTANGLE_TEST_TIME_PAUSE_BETWEEN_RECTANGLE"    : the pause between two rectangles are shown
    "RECTANGLE_TEST_TIME_PER_SEQUENCE"               : the time for the complete test sequence
    
3. Sequence Specific Information
These are information which are really specific to the current sequence. They change from file to file among the same test.
    "SEQ_NUMBER_OF_ROWS" : The number of rows for this sequence
    "SEQ_NUMBER_OF_COLS" : The number of cols for this sequence
    "SEQ_START_TIME"     : The time when the sequence started
    
4. Rectangle Specific Information
These are information for each single rectangle that is displayed. This is a JSON array consisting of the different rectangles
that were displayed. Each rectangle is a single JSON Object.
	"REC_TOP_LEFT_X"       : the top left x coordinate of the rectangle
	"REC_TOP_LEFT_Y"       : the top left y coordinate of the rectangle
	"REC_BOTTOM_RIGHT_X"   : the bottom right x coordinate of the rectangle
	"REC_BOTTOM_RIGHT_Y"   : the bottom right y coordinate of the rectangle
	"REC_COL"              : the column of the displayed rectangle
	"REC_ROW"              : the row of the displayed rectangle
	"REC_START_TIME"       : The time when the rectangle was displayed