The purpose of this file is to describe which values are recorded during the test. 

Each file has the filename:
testtype_MM_currenttime.json

testtype: screen test
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
    "SCREEN_TIME_ON_TABLET"              : Indicates the amount of time which we look on the display
    "SCREEN_TIME_BESIDE_TABLET"          : Indicates the amount of time which we look beside the display
    "SCREEN_TEST_NUMBER_OF_REPETITIONS"  : Indicates the number of repetitions for each point    
    
3. Action Specific Information
These are information for each single action that is performed. This is a JSON array consisting of the different actions
that were displayed. Each action is a single JSON Object.
	"SCREEN_ACTION"      : The performed action (SCREEN;LEFT;RIGHT;ABOVE)
	"SCREEN_START_TIME"  : The time when the action was displayed