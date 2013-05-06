package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.BaseAdapter;

public class UserRepliesAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    
    public UserRepliesAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row_user_replies, null);

        TextView gray = (TextView)vi.findViewById(R.id.gray); // title
        TextView time = (TextView)vi.findViewById(R.id.time); // node
        WebView reply =(WebView)vi.findViewById(R.id.reply); // thumb image
        TextView more = (TextView)vi.findViewById(R.id.more); // more      
        
        final HashMap<String, String> replyData = data.get(position);
           
        if (replyData.get(ApiClient.KEY_GRAY) != MainActivity.MORE_TAG) {
        	// Setting all values in listview
            gray.setText(replyData.get(ApiClient.KEY_GRAY));
            time.setText(replyData.get(ApiClient.KEY_TIME));
            
            StringBuilder sb = new StringBuilder();
			sb.append("<html><head>");
			sb.append("<style type=\"text/css\">body{color: #63656a }");
			sb.append("</style></head>");
			sb.append("<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">");
			sb.append(replyData.get(ApiClient.KEY_REPLY));
			sb.append("</body></html>");
            reply.loadDataWithBaseURL(null,
            		sb.toString(), "text/html", "UTF-8", null);
            reply.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.SINGLE_COLUMN);
			
            gray.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            reply.setVisibility(View.VISIBLE);
            more.setVisibility(View.GONE);
        } else {
        	gray.setVisibility(View.GONE);
        	time.setVisibility(View.GONE);
        	reply.setVisibility(View.GONE);
            more.setText(replyData.get(ApiClient.KEY_REPLY));
            more.setVisibility(View.VISIBLE);
        }
        
        return vi;
    }
}