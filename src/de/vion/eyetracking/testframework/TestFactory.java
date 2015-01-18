package de.vion.eyetracking.testframework;

import de.vion.eyetracking.testframework.generic.GenericTestFragment;
import de.vion.eyetracking.testframework.generic.GenericTestSettings;
import de.vion.eyetracking.testframework.tests.line.LineTestFragment;
import de.vion.eyetracking.testframework.tests.line.settings.LineTestGeneralSettings;
import de.vion.eyetracking.testframework.tests.live.LiveViewTestFragment;
import de.vion.eyetracking.testframework.tests.live.LiveViewTestSettings;
import de.vion.eyetracking.testframework.tests.point.PointTestFragment;
import de.vion.eyetracking.testframework.tests.point.settings.PointTestSettings;
import de.vion.eyetracking.testframework.tests.rectangle.RectangleTestFragment;
import de.vion.eyetracking.testframework.tests.rectangle.settings.RectangleTestSequenceSettings;
import de.vion.eyetracking.testframework.tests.rectangle.settings.RectangleTestSingleSettings;
import de.vion.eyetracking.testframework.tests.screen.ScreenTestFragment;
import de.vion.eyetracking.testframework.tests.screen.settings.ScreenTestGeneralSettings;
import de.vion.eyetracking.testframework.tests.video.VideoTestFragment;
import de.vion.eyetracking.testframework.tests.video.VideoTestSettings;
import de.vion.eyetracking.testframework.tests.word.WordTestFragment;
import de.vion.eyetracking.testframework.tests.word.settings.WordTestSequenceSettings;
import de.vion.eyetracking.testframework.tests.word.settings.WordTestSingleSettings;


/**
 * The class for generating a test based on the given test type
 * 
 * @author André Pomp
 *
 */
public class TestFactory {

	public static GenericTestFragment getTestFragment(TestType testType,
			String testSubjectAbbreviation, String subdir, boolean demoModus) {
		switch (testType) {
		case LIVE_VIEW_TEST:
			return LiveViewTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case RECTANGLE_SINGLE_TEST:
		case RECTANGLE_SEQUENCE_TEST:
			return RectangleTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case WORD_SINGLE_TEST:
		case WORD_SEQUENCE_TEST:
			return WordTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case VIDEO_TEST:
			return VideoTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case LINE_TEST:
			return LineTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case POINT_TEST:
			return PointTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);
		case SCREEN_TEST:
			return ScreenTestFragment.createInstance(testType,
					testSubjectAbbreviation, subdir, demoModus);

		}
		throw new RuntimeException("Invalid TestType");
	}

	public static GenericTestSettings getTestSettings(TestType testType) {
		switch (testType) {
		case LIVE_VIEW_TEST:
			return new LiveViewTestSettings();
		case RECTANGLE_SINGLE_TEST:
			return new RectangleTestSingleSettings();
		case RECTANGLE_SEQUENCE_TEST:
			return new RectangleTestSequenceSettings();
		case WORD_SINGLE_TEST:
			return new WordTestSingleSettings();
		case WORD_SEQUENCE_TEST:
			return new WordTestSequenceSettings();
		case VIDEO_TEST:
			return new VideoTestSettings();
		case LINE_TEST:
			return new LineTestGeneralSettings();
		case POINT_TEST:
			return new PointTestSettings();
		case SCREEN_TEST:
			return new ScreenTestGeneralSettings();
		}
		throw new RuntimeException("Invalid TestType");
	}
}