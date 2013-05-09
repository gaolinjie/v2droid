
package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MessageFragment extends Fragment {

    private static MessageFragment instance;
    private ProgressBar progressBar;
	private ProgressBar progressBar2;
	ArrayList<HashMap<String, String>> messageList = null;
    private ListView messageListView;
    MessageAdapter mMessagesAdapter = null;
    int recentPageNum = 1;
    Document doc;

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

        progressBar = (ProgressBar) view
				.findViewById(R.id.progress_bar);
        progressBar2 = (ProgressBar) view
				.findViewById(R.id.progress_bar2);
		
        if (messageList==null) {
        	messageList = new ArrayList<HashMap<String, String>>();
    		new GetDataTask().execute();
    		mMessagesAdapter = new MessageAdapter((Activity)getActivity(), messageList);
    		progressBar.setVisibility(View.VISIBLE);
    		progressBar2.setVisibility(View.GONE);
        } else {
        	progressBar.setVisibility(View.GONE);
        	progressBar2.setVisibility(View.GONE);
        }
        
        messageListView = (ListView) view
				.findViewById(R.id.message_list);
        messageListView.setAdapter(mMessagesAdapter);
        
        messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {

				if (messageList.get(position).get(
						ApiClient.KEY_MESSAGE) == MainActivity.MORE_TAG) {
					
					progressBar2.setVisibility(View.VISIBLE);
					new GetDataTask().execute();
				}
			}
		});			
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
			String url = "http://v2ex.com/notifications?p=" + recentPageNum;
		
			AppContext ac = (AppContext) getActivity().getApplication();
			
			Document doc;
			
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getMessages(ac, doc, messageList);
				
			} catch (IOException e) {
				
			}
			
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {

			if (!messageList.isEmpty()) {
				mMessagesAdapter.notifyDataSetChanged();
				recentPageNum++;
			}

			progressBar.setVisibility(View.GONE);
			progressBar2.setVisibility(View.GONE);

			super.onPostExecute(result);
		}
	}
}
