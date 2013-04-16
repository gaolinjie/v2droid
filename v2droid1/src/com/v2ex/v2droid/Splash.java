package com.v2ex.v2droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {
	
	public static final String SHOW_TOPIC = "com.v2ex.v2droid.action.SHOW_TOPIC";

	private final int SPLASH_DISPLAY_LENGHT = 1000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent mainIntent = new Intent(Splash.this, TopicActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
	}
}
