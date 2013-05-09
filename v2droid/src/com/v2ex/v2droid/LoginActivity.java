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
					AppConfig.getAppConfig(ac).setLogin(true);
					AppConfig.getAppConfig(ac).setUsername(username);
					ApiClient.storeCookies(ac);
					Intent intent = new Intent();
					intent.putExtra("username", "@" + username);
					intent.putExtra("messages", "0");

					LoginActivity.this.setResult(RESULT_OK, intent);
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

	public void LoginTest() throws ClientProtocolException, IOException {
		String html = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);

		CookieStore cookieStore = new BasicCookieStore();
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpGet httpget = new HttpGet("http://www.v2ex.com/signin");
		httpget.setHeader("Connection", "Keep-Alive");

		try {
			HttpResponse responce = httpClient.execute(httpget, localContext);
			int resStatu = responce.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					html = EntityUtils.toString(entity);
				}
			}
		} catch (Exception e) {
			System.out.println("访问[http://www.v2ex.com/signin出现异常!");
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		System.out.println("Get-Cookie=====>"
				+ localContext.getAttribute(ClientContext.COOKIE_STORE)
						.toString());
		String once = HtmlParser.getTopicOnce2(html);

		HttpClient httpClient2 = new DefaultHttpClient();

		HttpClientParams.setCookiePolicy(httpClient2.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		HttpPost post = new HttpPost("http://www.v2ex.com/signin");
		post.setHeader("Connection", "Keep-Alive");
		post.setHeader("Referer", "http://v2ex.com/signin");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("next", "/"));
		params.add(new BasicNameValuePair("u", "burnex"));
		params.add(new BasicNameValuePair("p", "003491"));
		params.add(new BasicNameValuePair("once", once));
		params.add(new BasicNameValuePair("next", "/"));

		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		HttpResponse httpResponse = httpClient2.execute(post, localContext);
		Header header = httpResponse.getFirstHeader("Set-Cookie");
		if (header != null) {
			System.out.println("Set-Cookie=====>" + header.getValue());
		}
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		System.out.println("Post-Cookie=====>"
				+ localContext.getAttribute(ClientContext.COOKIE_STORE)
						.toString());

		HttpClient httpClient3 = new DefaultHttpClient();

		HttpClientParams.setCookiePolicy(httpClient3.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);

		HttpGet httpget3 = new HttpGet("http://www.v2ex.com/");
		httpget3.setHeader("Connection", "Keep-Alive");

		try {
			HttpResponse responce3 = httpClient3
					.execute(httpget3, localContext);
			int resStatu = responce3.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {

				HttpEntity entity = responce3.getEntity();
				if (entity != null) {
					html = EntityUtils.toString(entity);
					System.out.println("html====>" + html);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient3.getConnectionManager().shutdown();
		}
		System.out.println("Get-Cookie=====>" + cookieStore.toString());

		PersistentCookieStore pcs = new PersistentCookieStore(this);
		List<Cookie> cookieList = cookieStore.getCookies();
		for (Cookie cookie : cookieList) {
			pcs.addCookie(cookie);
		}

	}
}
