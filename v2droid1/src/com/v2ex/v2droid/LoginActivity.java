package com.v2ex.v2droid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity {

	private static Button loginButton;
	private static Button exitButton;
	private ButtonListener bl = new ButtonListener();
	private EditText usernameEdit;  
    private EditText passwordEdit; 
    private Intent intent; 
    public static final String SHOW_TOPIC = "com.v2ex.v2droid.action.SHOW_TOPIC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//添加登陆按钮监听  
        loginButton = (Button)findViewById(R.id.login_button);  
        loginButton.setOnClickListener(bl);  
	}

	private class ButtonListener implements View.OnClickListener {
		public void onClick(View view) {
			if (view == loginButton) {
				usernameEdit = (EditText) findViewById(R.id.username_edit);
				passwordEdit = (EditText) findViewById(R.id.password_edit);

				if ((usernameEdit.getText().toString()).equals("test")
						&& (passwordEdit.getText().toString()).equals("test")) {
					intent = new Intent(SHOW_TOPIC);

					// 启动Activity
					startActivity(intent);
					finish();
				} else {
					Toast toast=Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT);  
					//显示toast信息  
					toast.show();  
				}
			}  else if (view == exitButton) {
				finish();
			}
		}
	}

}
