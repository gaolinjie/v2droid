
package com.v2ex.v2droid;

import java.io.InputStream;

import org.holoeverywhere.Setting;
import org.holoeverywhere.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.Window;

public class LoginActivity extends Activity {
	
	private static Button loginButton;
	private ButtonListener bl = new ButtonListener();
	private EditText usernameEdit;  
    private EditText passwordEdit; 
    private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		
		//添加登陆按钮监听  
        loginButton = (Button)findViewById(R.id.login_button);  
        loginButton.setOnClickListener(bl);

	}
	
	private class ButtonListener implements View.OnClickListener {
		public void onClick(View view) {
			//隐藏软键盘
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
			
			String username = usernameEdit.getText().toString();
			String password = passwordEdit.getText().toString();

			//判断输入
			if(StringUtils.isEmpty(username)){
				UIHelper.ToastMessage(view.getContext(), getString(R.string.msg_login_usr_null));
				return;
			}
			if(StringUtils.isEmpty(password)){
				UIHelper.ToastMessage(view.getContext(), getString(R.string.msg_login_pwd_null));
				return;
			}
			
			System.out.println("onClick=====>");
			
			login(username, password);
		}
	}
	
	//登录验证
    private void login(final String account, final String pwd) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				System.out.println("login handleMessage=====>");
				if(msg.what == 1){
					ApiClient.cleanCookie();
					
						finish();
					
				}else if(msg.what == 0){
					
					UIHelper.ToastMessage(LoginActivity.this, getString(R.string.msg_login_fail)+msg.obj);
				}else if(msg.what == -1){
				}
			}
		};
		new Thread(){
			public void run() {
				System.out.println("login run=====>");
				Message msg =new Message();
				try {
					AppContext ac = (AppContext)getApplication(); 
					String s = ac.loginVerify(account, pwd);
	                
	                if(!StringUtils.isEmpty(s)){
	                	msg.what = 1;//成功
	                	msg.obj = 1;
	                }else{
	                	msg.what = 0;//失败
	                	msg.obj = 0;
	                }
	            } catch (AppException e) {
	            	e.printStackTrace();
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				handler.sendMessage(msg);
			}
		}.start();
    }
}
