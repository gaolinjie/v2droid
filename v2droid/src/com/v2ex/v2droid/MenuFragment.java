package com.v2ex.v2droid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class MenuFragment extends SherlockFragment {

	private final static String TAG = "MenuFragment";
	private static final String KEY_CONTENT = "MenuFragment:Content";
	private static final String NAME_COLUMN = "name";
	private static final String NUM_COLUMN = "num";
	private static final String SUM_COLUMN = "sum";



	Context mContext;

	public static MenuFragment newInstance(String text) {
		MenuFragment fragment = new MenuFragment();

		fragment.mContent = text;
		Log.i(TAG, fragment.mContent);

		return fragment;
	}

	private String mContent = "???";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view;
	    view = inflater.inflate(R.layout.fragment_menu, null);
	    
	    SlideMenuItem slideMenuItem_data[] = new SlideMenuItem[]
	            {
	                new SlideMenuItem(R.drawable.ic_action_view_as_list, "主题"),
	                new SlideMenuItem(R.drawable.ic_action_view_as_grid, "节点"),
	                new SlideMenuItem(R.drawable.ic_action_favourite, "收藏"),
	                new SlideMenuItem(R.drawable.ic_action_message, "通知"),
	                new SlideMenuItem(R.drawable.ic_action_settings, "设置"),
	                new SlideMenuItem(R.drawable.ic_action_person, "账户"),
	                new SlideMenuItem(R.drawable.ic_action_search, "搜索"),
	                new SlideMenuItem(R.drawable.ic_action_website, "V2DROID"),
	            };
	            
	    ListViewAdapter adapter = new ListViewAdapter(mContext, 
	                    R.layout.listview_row, slideMenuItem_data);
		
		ListView lv = (ListView) view.findViewById(R.id.listview_menu);
		
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

		mContext = getActivity();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	public class SlideMenuItem {
	    public int icon;
	    public String title;
	    public SlideMenuItem(){
	        super();
	    }
	    
	    public SlideMenuItem(int icon, String title) {
	        super();
	        this.icon = icon;
	        this.title = title;
	    }
	}
	
	public class ListViewAdapter extends ArrayAdapter<SlideMenuItem>{

	    Context context; 
	    int layoutResourceId;    
	    SlideMenuItem data[] = null;
	    
	    public ListViewAdapter(Context context, int layoutResourceId, SlideMenuItem[] data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        SlideMenuItemHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new SlideMenuItemHolder();
	            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
	            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
	            
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (SlideMenuItemHolder)row.getTag();
	        }
	        
	        SlideMenuItem slideMenuItem = data[position];
	        holder.txtTitle.setText(slideMenuItem.title);
	        holder.imgIcon.setImageResource(slideMenuItem.icon);
	        
	        return row;
	    }
	    
	    class SlideMenuItemHolder
	    {
	        ImageView imgIcon;
	        TextView txtTitle;
	    }
	}
}
