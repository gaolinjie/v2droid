package com.v2ex.v2droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 1000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				boolean isLogin = AppConfig.getLogin(Splash.this);
				
				Intent intent;
				if (isLogin) {
					intent = new Intent(Splash.this, MainActivity.class);
				} else {
					intent = new Intent(Splash.this, LoginActivity.class);
				}
				
				Splash.this.startActivity(intent);
				Splash.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
	}
}
