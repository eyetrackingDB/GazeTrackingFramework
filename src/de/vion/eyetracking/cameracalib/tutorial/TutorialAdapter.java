package de.vion.eyetracking.cameracalib.tutorial;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.vion.eyetracking.R;

/**
 * The Adapter for the single parts of the tutorial
 * 
 * @author André Pomp
 */
public class TutorialAdapter extends FragmentStatePagerAdapter {

	private List<TutorialFragment> items = new ArrayList<TutorialFragment>();

	public TutorialAdapter(FragmentManager fm) {
		super(fm);
		this.items.add(TutorialFragment
				.getInstance(R.layout.tutorial_fragment_page_1));
		this.items.add(TutorialFragment
				.getInstance(R.layout.tutorial_fragment_page_2));
		this.items.add(TutorialFragment
				.getInstance(R.layout.tutorial_fragment_page_3));
		this.items.add(TutorialFragment
				.getInstance(R.layout.tutorial_fragment_page_4));
	}

	@Override
	public Fragment getItem(int arg0) {
		return this.items.get(arg0);
	}

	@Override
	public int getCount() {
		return this.items.size();
	}
}
