package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
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

        TextView username = (TextView)vi.findViewById(R.id.username); // username
        TextView reply = (TextView)vi.findViewById(R.id.reply); // reply
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> replyRow = new HashMap<String, String>();
        replyRow = data.get(position);
        
        // Setting all values in listview
        username.setText(replyRow.get(ContentFragment.KEY_USERNAME));
        reply.setText(replyRow.get(ContentFragment.KEY_CONTENT));
        imageLoader.DisplayImage(replyRow.get(ContentFragment.KEY_AVATAR), thumb_image);
        return vi;
    }
}