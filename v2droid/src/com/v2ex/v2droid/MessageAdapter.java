package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MessageAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public MessageAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
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
            vi = inflater.inflate(R.layout.list_row_message, null);

        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
        LinearLayout avatar_layout = (LinearLayout)vi.findViewById(R.id.avatar);
        
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView time = (TextView)vi.findViewById(R.id.time); // node
        WebView message =(WebView)vi.findViewById(R.id.message); // thumb image
        TextView more = (TextView)vi.findViewById(R.id.more); // more      
        
        final HashMap<String, String> messageData = data.get(position);
           
        if (messageData.get(ApiClient.KEY_MESSAGE) != MainActivity.MORE_TAG) {
        	// Setting all values in listview
        	imageLoader.DisplayImage(messageData.get(TopicFragment.KEY_AVATAR), thumb_image);
        	
            title.setText(messageData.get(ApiClient.KEY_TITLE));
            time.setText(messageData.get(ApiClient.KEY_TIME));
            
            StringBuilder sb = new StringBuilder();
			sb.append("<html><head>");
			sb.append("<style type=\"text/css\">body{color: #63656a }");
			sb.append("</style></head>");
			sb.append("<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">");
			sb.append(messageData.get(ApiClient.KEY_MESSAGE));
			sb.append("</body></html>");
			message.loadDataWithBaseURL(null,
            		sb.toString(), "text/html", "UTF-8", null);
			message.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.SINGLE_COLUMN);
			
			thumb_image.setVisibility(View.VISIBLE);
	        avatar_layout.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            more.setVisibility(View.GONE);
        } else {
        	thumb_image.setVisibility(View.GONE);
            avatar_layout.setVisibility(View.GONE);
        	title.setVisibility(View.GONE);
        	time.setVisibility(View.GONE);
        	message.setVisibility(View.GONE);
            more.setText(messageData.get(ApiClient.KEY_TITLE));
            more.setVisibility(View.VISIBLE);
        }
        
        return vi;
    }
}