
package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
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
	ArrayList<HashMap<String, String>> messageList = null;
	ArrayList<HashMap<String, String>> tempList = null;
    private ListView messageListView;
    MessageAdapter mMessagesAdapter = null;
    int recentPageNum = 1;
    Document doc;
    private MenuItem refresh;
    boolean bRefresh = false;
    boolean bIsLastPage = false;
	boolean bNotLoag = false;

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
        progressBar.setVisibility(View.GONE);
		
        if (messageList==null) {
        	messageList = new ArrayList<HashMap<String, String>>();
        	tempList = new ArrayList<HashMap<String, String>>();
    		new GetDataTask().execute();
    		mMessagesAdapter = new MessageAdapter((Activity)getActivity(), messageList);
    		progressBar.setVisibility(View.GONE);
        }
        
        messageListView = (ListView) view
				.findViewById(R.id.message_list);
        messageListView.setAdapter(mMessagesAdapter);
        messageListView.setFocusable(true);
        
        messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {

				if (messageList.get(position).get(
						ApiClient.KEY_MESSAGE) == MainActivity.MORE_TAG) {
					
					progressBar.setVisibility(View.VISIBLE);
					new GetDataTask().execute();
				} else {
					String tid = messageList.get(position).get(
							TopicFragment.KEY_ID);
					
					Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
					contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
					contentIntent.putExtra("EXTRA_NODE_NAME", "");
					getActivity().startActivity(contentIntent);
				}
			}
		});			
    }
    
    @Override
    public void onCreateOptionsMenu(
          Menu menu, MenuInflater inflater) {
          inflater.inflate(R.menu.fragment_message, menu);
          refresh = menu.findItem(R.id.refresh);
          if (messageList==null || messageList.isEmpty()) {
       	   refresh.setActionView(R.layout.refresh);
          }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		((MainActivity)getActivity()).toggle();
        		break;
        		
        	case R.id.refresh:
            	refresh.setActionView(R.layout.refresh);
            	onRefresh();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void onRefresh() {
    	refresh.setActionView(R.layout.refresh);
    	bRefresh = true;
    	bNotLoag = false;
		bIsLastPage = false;
    	recentPageNum = 1;
    	new GetDataTask().execute();
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			
			if (!bIsLastPage) {
				String url = "http://v2ex.com/notifications?p=" + recentPageNum;
				AppContext ac = (AppContext) getActivity().getApplication();		
				Document doc;
				tempList.clear();	
				try {
					doc = ApiClient.get(ac, url, URLs.HOST).parse();
					bIsLastPage = ApiClient.getMessages(ac, doc, tempList);	
				} catch (IOException e) {
					
				}	
			} else {
				bNotLoag = true;
			}
	
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			progressBar.setVisibility(View.GONE);
			if (bNotLoag) {				
				Toast.makeText(getActivity().getApplicationContext(), "没有更多啦...",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (tempList.size() > 1) {
				System.out.println("!tempList.isEmpty()======>");
				if (bRefresh) {
					messageList.clear();
					bRefresh = false;
					if (!messageListView.isStackFromBottom()) {
						messageListView.setStackFromBottom(true);
					}
					messageListView.setStackFromBottom(false);
				}
				
				if (!messageList.isEmpty()) {
					messageList.remove(messageList.size() - 1);
				}
				
				for (int i=0; i < tempList.size(); i++) {
					messageList.add(tempList.get(i));
				}
				recentPageNum++;
				mMessagesAdapter.notifyDataSetChanged();
			} else {
				if (getActivity()!=null) {
					Toast.makeText(getActivity().getApplicationContext(),
							"貌似网络不给力啊...", Toast.LENGTH_SHORT).show();
				}
			}

			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}
}
