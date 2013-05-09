package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.slidingmenu.SlidingActivity;
import org.holoeverywhere.slidingmenu.SlidingMenu;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.readystatesoftware.viewbadger.BadgeView;

public class MainActivity extends SlidingActivity {
	public static String TOPIC_ID = null;
	public static String MORE_TAG;

	private final class ListNavigationAdapter extends
			ArrayAdapter<MainNavigationItem> implements OnItemClickListener {
		private int lastSelectedItem = 0;
		private int preSelectedItem = -1;
		private View messageView = null;
		private TextView usernameView = null;
		BadgeView badge = null;

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

			if (item.title == R.string.message) {
				messageView = view.findViewById(R.id.selectionHandler2);
			}

			if (item.title == R.string.user) {
				usernameView = (TextView) view.findViewById(android.R.id.text1);
				String username = ((AppContext) getApplication()).getUsername();
				setUsername(username);
			}

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
				} else {
					Intent intent = new Intent(MainActivity.this,
							UserActivity.class);
					intent.putExtra("EXTRA_USER_ID", getUsername());
					startActivity(intent);
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

		public void setMessageNum(String messageNum) {
			System.out.println("setMessageNum");
			if (messageView == null) {
				System.out.println("messageView==null || messageNum==null");
				return;
			}

			if (badge == null) {
				badge = new BadgeView(MainActivity.this, messageView);
				badge.setBadgeBackgroundColor(Color.parseColor("#FF4444"));
				badge.setBadgePosition(BadgeView.POSITION_CENTER);
			}

			if (messageNum == null) {
				badge.hide();

			} else {
				badge.setText(messageNum);
				badge.show();
			}
		}

		public void setUsername(String username) {
			if (usernameView == null || username == null) {
				return;
			}
			usernameView.setText(username);
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
		adapter.add(FavoriteFragment.class, R.string.favorite);
		adapter.add(NodeFragment.class, R.string.node);
		adapter.add(AboutFragment.class, R.string.setting);
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

	public void setDarkTheme(View v) {
		ThemeManager.restartWithDarkTheme(this);
	}

	public void setLightTheme(View v) {
		ThemeManager.restartWithLightTheme(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		adapter.onBackPressed();
	}

	public boolean checkIsLogin() {
		boolean isLogin = ((AppContext) getApplication()).getLogin();
		if (!isLogin) {
			startActivityForResult(new Intent(MainActivity.this,
					LoginActivity.class), 1);
		}

		return isLogin;
	}
	
	public String getUsername() {
		String username = ((AppContext) getApplication()).getUsername();
		return username;
	}

	public void setMessageNum(String n) {
		adapter.setMessageNum(n);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			String username = data.getExtras().getString("username");
			String messages = data.getExtras().getString("messages");

			adapter.setUsername(username);
			// adapter.setMessageNum(messages);
			break;
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
