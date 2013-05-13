package com.v2ex.v2droid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ProgressBar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.Window;
import com.loopj.android.http.PersistentCookieStore;

public class LoginActivity extends Activity {

	private static Button loginButton;
	private ButtonListener bl = new ButtonListener();
	private EditText usernameEdit;
	private EditText passwordEdit;
	private InputMethodManager imm;
	private ProgressBar progressBar;

	String username;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);

		// 添加登陆按钮监听
		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(bl);
	}

	private class ButtonListener implements View.OnClickListener {
		public void onClick(View view) {
			progressBar.setVisibility(View.VISIBLE);
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			username = usernameEdit.getText().toString();
			password = passwordEdit.getText().toString();

			// 判断输入
			if (StringUtils.isEmpty(username)) {
				UIHelper.ToastMessage(view.getContext(),
						getString(R.string.msg_login_usr_null));
				return;
			}
			if (StringUtils.isEmpty(password)) {
				UIHelper.ToastMessage(view.getContext(),
						getString(R.string.msg_login_pwd_null));
				return;
			}

			System.out.println("onClick=====>");

			new GetDataTask().execute();
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };

			System.out.println("username=====>" + username);

			System.out.println("password=====>" + password);

			AppContext ac = (AppContext) getApplication();

			try {
				if (ApiClient.login(ac, username, password)) {
					AppConfig.setLogin(LoginActivity.this, true);
					AppConfig.setUsername(LoginActivity.this, username);
					ApiClient.storeCookies(ac);
					
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);					
					LoginActivity.this.startActivity(intent);
					
					LoginActivity.this.finish();
				}

			} catch (IOException e) {

			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {

			super.onPostExecute(result);
		}
	}
}
