package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.slidingmenu.SlidingActivity;
import org.holoeverywhere.slidingmenu.SlidingMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;

public class MainActivity extends SlidingActivity {
	public static String TOPIC_ID = null;
	public static String MORE_TAG;

	public static final String SHOW_LOGIN = "com.v2ex.v2droid.action.SHOW_LOGIN";

	private final class ListNavigationAdapter extends
			ArrayAdapter<MainNavigationItem> implements OnItemClickListener {
		private int lastSelectedItem = 0;
		private int preSelectedItem = -1;

		public ListNavigationAdapter() {
			this(new ArrayList<MainNavigationItem>());
		}

		public ListNavigationAdapter(List<MainNavigationItem> list) {
			super(MainActivity.this, android.R.id.text1, list);
		}

		public void add(Class<? extends Fragment> clazz, int title) {
			add(new MainNavigationItem(clazz, title));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			NavigationItem view;
			if (convertView == null) {
				view = new NavigationItem(MainActivity.this);
				view.setSelectionHandlerColorResource(R.color.holo_blue_light);
			} else {
				view = (NavigationItem) convertView;
			}
			MainNavigationItem item = getItem(position);
			view.setLabel(item.title);
			view.setSelectionHandlerVisiblity(lastSelectedItem == position ? View.VISIBLE
					: View.INVISIBLE);
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int itemPosition, long itemId) {
			int preSelected = preSelectedItem;
			preSelectedItem = lastSelectedItem;
			lastSelectedItem = itemPosition;

			getIntent().putExtra(LIST_NAVIGATION_PAGE, itemPosition);

			MainNavigationItem item = getItem(itemPosition);
			if (item.title == R.string.user) {
				if (!checkIsLogin()) {
					return;
				}
			}

			notifyDataSetInvalidated();

			replaceFragment(item.getFragment());

			getSupportActionBar().setTitle(item.title);

			getSlidingMenu().showAbove(true);
		}

		public void onBackPressed() {
			lastSelectedItem = preSelectedItem;
			notifyDataSetInvalidated();
		}

	}

	private static final class MainNavigationItem {
		public final Class<? extends Fragment> clazz;
		private Fragment fragment;
		public final int title;

		public MainNavigationItem(Class<? extends Fragment> clazz, int title) {
			this.clazz = clazz;
			this.title = title;
		}

		public Fragment getFragment() {
			if (fragment == null) {
				try {
					fragment = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return fragment;
		}
	}

	private static final String LIST_NAVIGATION_PAGE = "listNavigationPage";
	private ListNavigationAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MORE_TAG = getResources().getString(R.string.more);

		if (adapter == null) {
			adapter = new ListNavigationAdapter();
		} else {
			adapter.clear();
		}

		adapter.add(TopicFragment.class, R.string.topic);
		adapter.add(UserFragment.class, R.string.user);
		adapter.add(MessageFragment.class, R.string.message);
		adapter.add(UserFragment.class, R.string.favorite);
		adapter.add(NodeFragment.class, R.string.node);
		adapter.add(UserFragment.class, R.string.setting);
		adapter.add(AboutFragment.class, R.string.about);

		NavigationWidget navigationWidget = new NavigationWidget(this);
		navigationWidget.init(adapter, adapter, ThemeManager.getTheme(this),
				getIntent().getIntExtra(LIST_NAVIGATION_PAGE, 0));
		setBehindContentView(navigationWidget);

		final SlidingMenu si = getSlidingMenu();
		si.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		si.setBehindWidthRes(R.dimen.demo_menu_width);
		si.setShadowWidth(0);

		final ActionBar ab = getSupportActionBar();
		// ab.setTitle(R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * 
	 * getSupportMenuInflater().inflate(R.menu.main, menu); return true;
	 * 
	 * }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: toggle(); break;
	 * 
	 * default: return super.onOptionsItemSelected(item); } return true; }
	 */
	public void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, null);
	}

	public void replaceFragment(Fragment fragment, String backStackName) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(android.R.id.content, fragment);
		if (backStackName != null) {
			ft.addToBackStack(backStackName);
		}
		ft.commit();
	}

	public boolean checkIsLogin() {
		final AppContext ac = (AppContext) getApplication();
		if (!ac.isLogin()) {
			Intent intent = new Intent(SHOW_LOGIN);
			startActivity(intent);
		}

		return ac.isLogin();
	}

	public void setDarkTheme(View v) {
		ThemeManager.restartWithDarkTheme(this);
	}

	public void setLightTheme(View v) {
		ThemeManager.restartWithLightTheme(this);
	}

	public void setMixedTheme(View v) {
		ThemeManager.restartWithMixedTheme(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		adapter.onBackPressed();
	}
}
