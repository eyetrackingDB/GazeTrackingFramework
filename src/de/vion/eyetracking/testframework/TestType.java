package de.vion.eyetracking.testframework;

import java.util.ArrayList;
import java.util.List;

import de.vion.eyetracking.R;


/**
 * The enums for the possible test types and their corresponding settings
 * 
 * @author André Pomp
 *
 */
public enum TestType {

	// Live view has no settings
	LIVE_VIEW_TEST(R.string.test_type_name_test_live),

	// Line Test
	LINE_TEST(R.string.test_type_name_test_line, R.xml.line_preferences),

	// Rectangle test has generic and test specific settings
	RECTANGLE_SINGLE_TEST(R.string.test_type_name_test_rectangle_single,
			R.xml.rectangle_preferences, R.xml.rectangle_single_preferences), RECTANGLE_SEQUENCE_TEST(
			R.string.test_type_name_test_rectangle_sequence,
			R.xml.rectangle_preferences, R.xml.rectangle_sequence_preferences),

	// Word test has generic and test specific settings
	WORD_SINGLE_TEST(R.string.test_type_name_test_word_single,
			R.xml.word_preferences, R.xml.word_single_preferences), WORD_SEQUENCE_TEST(
			R.string.test_type_name_test_word_sequence, R.xml.word_preferences),

	// Video test only has single settings
	VIDEO_TEST(R.string.test_type_name_test_video, R.xml.video_preferences),

	// Point test only has single settings
	POINT_TEST(R.string.test_type_name_test_point, R.xml.point_preferences),

	// Point test only has single settings
	SCREEN_TEST(R.string.test_type_name_test_screen, R.xml.screen_preferences);

	private int nameResource;
	private List<Integer> settingsResources;

	private TestType(int nameResource, int... settingsResources) {
		this.nameResource = nameResource;
		this.settingsResources = new ArrayList<Integer>();
		for (int settingsResource : settingsResources) {
			this.settingsResources.add(settingsResource);
		}
	}

	/**
	 * @return the nameResource
	 */
	public int getNameResource() {
		return this.nameResource;
	}

	public List<Integer> getSettingsResources() {
		return this.settingsResources;
	}
}
