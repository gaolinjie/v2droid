
package com.v2ex.v2droid;

import java.io.IOException;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MessageFragment extends Fragment {

    private static MessageFragment instance;
    String messages = null;
    private WebView mMessagesListView = null;
    private ProgressBar progressBar;
    TextView nomessageView;

    public static MessageFragment getInstance() {
        if (MessageFragment.instance == null) {
            return new MessageFragment();
        }
        return MessageFragment.instance;
    }

    public MessageFragment() {
    	MessageFragment.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        //view.findViewById(R.id.github).setOnClickListener(githubListener);
        //view.findViewById(R.id.developers).setOnClickListener(developersListener);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.VISIBLE);
		
        mMessagesListView = (WebView) view.findViewById(R.id.messages_list);
        mMessagesListView.setVisibility(View.GONE);
        
        nomessageView = (TextView) view.findViewById(R.id.nomessage_text);
        nomessageView.setVisibility(View.GONE);
        
        new GetDataTask().execute();
    }
    
    @Override
    public void onCreateOptionsMenu(
          Menu menu, MenuInflater inflater) {
       //inflater.inflate(R.menu.fragment_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		((MainActivity)getActivity()).toggle();
        		break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/notifications";
		
			AppContext ac = (AppContext) getActivity().getApplication();
			
			Document doc;
			
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				messages = ApiClient.getMessages(ac, doc);
				
			} catch (IOException e) {
				
			}
			

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {

			progressBar.setVisibility(View.GONE);
			
			if (messages == null) {
				nomessageView.setVisibility(View.VISIBLE);
			} else {
				mMessagesListView.loadDataWithBaseURL(null,
						messages, "text/html", "UTF-8", null);
				mMessagesListView.getSettings().setLayoutAlgorithm(
						LayoutAlgorithm.SINGLE_COLUMN);
			
				mMessagesListView.setVisibility(View.VISIBLE);
			}
			
			

			super.onPostExecute(result);
		}
	}
}
