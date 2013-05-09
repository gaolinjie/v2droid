package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RepliesAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public RepliesAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.list_row_replies, null);

        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        TextView username = (TextView)vi.findViewById(R.id.username); // username
        TextView time = (TextView)vi.findViewById(R.id.time); // username
        TextView floor = (TextView)vi.findViewById(R.id.floor); // username
        TextView reply = (TextView)vi.findViewById(R.id.reply); // reply
       
        HashMap<String, String> replyRow = new HashMap<String, String>();
        replyRow = data.get(position);
        
        // Setting all values in listview
        imageLoader.DisplayImage(replyRow.get(ApiClient.KEY_AVATAR), thumb_image);
    	
        username.setText(replyRow.get(ApiClient.KEY_USERNAME));
        time.setText(replyRow.get(ApiClient.KEY_TIME));
        floor.setText(replyRow.get(ApiClient.KEY_FLOOR));
        
        StringBuilder sb = new StringBuilder();
		sb.append("<html><head>");
		sb.append("<style type=\"text/css\">body{color: #63656a }");
		sb.append("</style></head>");
		sb.append("<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">");
		sb.append(replyRow.get(ApiClient.KEY_REPLY));
		sb.append("</body></html>");
		//reply.loadDataWithBaseURL(null,
        //		sb.toString(), "text/html", "UTF-8", null);
		//reply.getSettings().setLayoutAlgorithm(
		//		LayoutAlgorithm.SINGLE_COLUMN);
		reply.setText(Html.fromHtml(sb.toString()));
        return vi;
    }
}