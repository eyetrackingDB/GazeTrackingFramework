The purpose of this file is to describe which values are recorded during the test. 

Each file has the filename:
testtype_xy_currenttime.json

testtype: word_single_test or word_sequence_test (depends on which test was chosen)
xy: The front size that was used during this sequence
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
	"WORD_TEST_TIME_WORD"                    : The amount of time for which a word is highlighted
    "WORD_NUMBER_OF_WORDS"                   : The number of words during this test
    "WORD_TEST_SEQUENCE": true,              : A JSON Object which describes the sequence during this run (see 3)
    
3. Sequence Specific Information
These are information which are really specific to the current sequence. They change from file to file among the same test.
    "SEQ_FONT"        : The font that was used during this sequence (e.g., Sansif-Bold)
    "SEQ_FONT_SIZE"   : The font size that was used during this sequence
    "SEQ_START_TIME"  : The time when the sequence started
    "SEQ_WORDS"       : A JSON array that describes the words that were displayed (see 4)
    
4. Word Specific Information
These are information for each single rectangle that is displayed. This is a JSON array consisting of the different rectangles
that were displayed. Each rectangle is a single JSON Object.
	"WORD_TOP_LEFT_X"     : The top left x coordinate of the bounding box of the word
	"WORD_TOP_LEFT_Y"     : The top left y coordinate of the bounding box of the word
	"WORD_BOTTOM_RIGHT_X" : The bottom right x coordinate of the bounding box of the word
	"WORD_BOTTOM_RIGHT_Y" : The bottom right y coordinate of the bounding box of the word
	"WORD_START_TIME"     : The starting time when the word was displayed