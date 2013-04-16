
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

public class LoginFragment extends Fragment {

    private static LoginFragment instance;
    
    private static Button loginButton;
	private static Button exitButton;
	private ButtonListener bl = new ButtonListener();
	private EditText usernameEdit;  
    private EditText passwordEdit; 
    private Intent intent; 
    
    Context mContext;

    public static LoginFragment getInstance() {
        if (LoginFragment.instance == null) {
            return new LoginFragment();
        }
        return LoginFragment.instance;
    }

    public LoginFragment() {
    	LoginFragment.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = getActivity();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = null;
    	view = inflater.inflate(R.layout.fragment_login, null);
    	return view;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        loginButton = (Button)view.findViewById(R.id.login_button);  
        loginButton.setOnClickListener(bl);  
    }
    
    private class ButtonListener implements View.OnClickListener {
		public void onClick(View view) {
			if (view == loginButton) {
				usernameEdit = (EditText) getActivity().findViewById(R.id.username_edit);
				passwordEdit = (EditText) getActivity().findViewById(R.id.password_edit);

				if ((usernameEdit.getText().toString()).equals("test")
						&& (passwordEdit.getText().toString()).equals("test")) {
					//intent = new Intent(SHOW_TOPIC);

					// 启动Activity
					//startActivity(intent);
					//finish();
				} else {
					Toast toast=Toast.makeText(mContext, "用户名或密码错误", Toast.LENGTH_SHORT);  
					//显示toast信息  
					toast.show();  
				}
			}  else if (view == exitButton) {
				//finish();
			}
		}
	}
}
