package de.vion.eyetracking.cameracalib.tutorial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import de.vion.eyetracking.R;
import de.vion.eyetracking.settings.GazeTrackingPreferences;

/**
 * The MainActivity for the tutorial
 * 
 * * @author André Pomp
 */
public class TutorialActivity extends FragmentActivity implements
		OnClickListener, OnPageChangeListener {

	// The view pager
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;

	// Buttons and checkbox
	private Button btnSkip;
	private CheckBox cbShowAgain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(GazeTrackingPreferences
				.getDeviceOrientationAsActivityInfo(this));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.tutorial_activity);

		this.viewPager = (ViewPager) findViewById(R.id.activity_tutorial_viewpager);
		this.pagerAdapter = new TutorialAdapter(getSupportFragmentManager());
		this.viewPager.setAdapter(this.pagerAdapter);
		this.viewPager.setOnPageChangeListener(this);

		this.btnSkip = (Button) findViewById(R.id.activity_tutorial_btn_skip);
		this.btnSkip.setOnClickListener(this);
		this.cbShowAgain = (CheckBox) findViewById(R.id.activity_tutorial_cb);
	}

	@Override
	public void onBackPressed() {
		if (this.viewPager.getCurrentItem() == 0) {
			super.onBackPressed();
		} else {
			this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_tutorial_btn_skip:
			GazeTrackingPreferences.setStartTutorial(TutorialActivity.this,
					this.cbShowAgain.isChecked());
			finish();
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// do nothing
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// do nothing
	}

	@Override
	public void onPageSelected(int arg0) {
		if ((arg0 + 1) == this.pagerAdapter.getCount()) {
			this.btnSkip
					.setText(getString(R.string.activity_tutorial_btn_finish));
		} else {
			this.btnSkip
					.setText(getString(R.string.activity_tutorial_btn_skip));
		}
	}
}