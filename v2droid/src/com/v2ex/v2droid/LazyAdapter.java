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
import android.widget.BaseAdapter;
import android.widget.ImageView;



public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView username = (TextView)vi.findViewById(R.id.username); // username
        TextView replies = (TextView)vi.findViewById(R.id.replies); // replies
        TextView node = (TextView)vi.findViewById(R.id.node); // node
        TextView time = (TextView)vi.findViewById(R.id.time); // node
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        TextView more = (TextView)vi.findViewById(R.id.more); // more
        LinearLayout avatar_layout = (LinearLayout)vi.findViewById(R.id.avatar);
        
        HashMap<String, String> topic = new HashMap<String, String>();
        topic = data.get(position);
        
        if (topic.get(TopicFragment.KEY_ID) != MainActivity.MORE_TAG) {
        	// Setting all values in listview
            title.setText(topic.get(TopicFragment.KEY_TITLE));
            username.setText(topic.get(TopicFragment.KEY_USERNAME));
            replies.setText(topic.get(TopicFragment.KEY_REPLIES));
            node.setText(topic.get(TopicFragment.KEY_NODE));
            time.setText(topic.get(TopicFragment.KEY_TIME));
            imageLoader.DisplayImage(topic.get(TopicFragment.KEY_AVATAR), thumb_image);
            title.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            replies.setVisibility(View.VISIBLE);
            node.setVisibility(View.VISIBLE);
            thumb_image.setVisibility(View.VISIBLE);
            avatar_layout.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            more.setVisibility(View.GONE);
        } else {
        	title.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            replies.setVisibility(View.GONE);
            node.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            thumb_image.setVisibility(View.GONE);
            avatar_layout.setVisibility(View.GONE);
            more.setText(topic.get(TopicFragment.KEY_TITLE));
            more.setVisibility(View.VISIBLE);
        }
        
        return vi;
    }
}