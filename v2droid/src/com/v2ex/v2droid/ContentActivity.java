package com.v2ex.v2droid;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ContentActivity extends SlidingFragmentActivity {

	private final static String TAG = "ContentActivity";

	protected MenuFragment mMenuFrag;
	
	SlidingMenu mSlidingMenu = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the Above View
		setContentView(R.layout.activity_content);
		this.getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_content, new ContentFragment()).commit();

		// set the Behind View
		setBehindContentView(R.layout.fragment_menu);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		mMenuFrag = new MenuFragment();
		t.replace(R.id.fragment_menu, mMenuFrag);
		t.commit();

		// customize the SlidingMenu
		this.setSlidingActionBarEnabled(false);

		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.activity_content, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mSlidingMenu.isMenuShowing()) {
				toggle();
			}
			else {
				finish();
			}			
			return true;
		case R.id.refresh:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
