package de.vion.eyetracking.testmanagement.list;

import de.vion.eyetracking.testframework.TestType;

/**
 * 
 * The callback that is called when a listitem is clicked
 * 
 * @author André Pomp
 * 
 */
public interface TestManagementListItemCallback {

	public void onTestItemClicked(TestType testtype, String directory);

}
