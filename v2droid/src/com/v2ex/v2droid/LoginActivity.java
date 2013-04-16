
package com.v2ex.v2droid;

import org.holoeverywhere.app.Activity;

import android.os.Bundle;

import com.actionbarsherlock.view.Window;


public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

	}
}
