package com.v2ex.v2droid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class TopicActivity extends SlidingFragmentActivity {

	private final static String TAG = "TopicActivity";
	private String[] TOPIC_TYPE = new String[] { "主题", "类别" };

	TopicFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	protected MenuFragment mFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		setContentView(R.layout.activity_topic);

		mAdapter = new TopicFragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		// set the Behind View
		setBehindContentView(R.layout.fragment_topic);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		mFrag = new MenuFragment();
		t.replace(R.id.fragment_topic, mFrag);
		t.commit();

		// customize the SlidingMenu
		this.setSlidingActionBarEnabled(false);

		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.activity_topic, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.refresh:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class TopicFragmentAdapter extends FragmentPagerAdapter {
		private int mCount = TOPIC_TYPE.length;

		public TopicFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TopicFragment.newInstance(TOPIC_TYPE[position % mCount]);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TOPIC_TYPE[position % mCount];
		}
	}

	public String[] getTopicType() {
		String[] s = new String[] {};
		return s;
	}
}
