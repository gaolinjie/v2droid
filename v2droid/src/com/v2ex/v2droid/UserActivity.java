
package com.v2ex.v2droid;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;


public class UserActivity extends Activity {
	
	private final static String TAG = "UserActivity";

	Context mContext;
	String userID;
	
	private String[] USER_TYPE = new String[] { "主题", "回复" };

	UserFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		mContext = this;
		
		final ActionBar ab = getSupportActionBar();
        //ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		userID = intent.getStringExtra("EXTRA_USER_ID");
		System.out.println("访问[@@@@@" + userID);
		
		mAdapter = new UserFragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);


	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getSupportMenuInflater().inflate(R.menu.activity_user, menu);
            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
                
            case R.id.refresh:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    class UserFragmentAdapter extends FragmentPagerAdapter {
		private int mCount = USER_TYPE.length;

		public UserFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return UserFragment.newInstance(USER_TYPE[position % mCount]);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return USER_TYPE[position % mCount];
		}
	}
}
