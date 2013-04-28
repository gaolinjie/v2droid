package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

/**
 * @author Tonic Artos
 * 
 * @param <T>
 */
public class NodeStickyAdapter<T> extends BaseAdapter
		implements StickyGridHeadersSimpleAdapter {
	private int mHeaderResId;
	private LayoutInflater mInflater;
	private int mItemResId;
	private ArrayList<T> mItems;

	static final String KEY_ID = "id";
	static final String KEY_HEADER_ID = "header_id";
	static final String KEY_HEADER = "header";
	static final String KEY_NAME = "name";
	static final String KEY_LINK = "LINK";

	public NodeStickyAdapter(Context context,
			ArrayList<T> items, int headerResId, int itemResId) {
		init(context, items, headerResId, itemResId);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public long getHeaderId(int position) {
		T item = getItem(position);
		String value;
		HashMap<String, String> map = (HashMap<String, String>) item;
		value = map.get(KEY_HEADER_ID);
		int id = 0;
		try {
			id = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			e.toString();
		}
		return id;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mHeaderResId, parent, false);
			holder = new HeaderViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		T item = getItem(position);
		String value;

		HashMap<String, String> map = (HashMap<String, String>) item;
		value = map.get(KEY_HEADER);

		// set header text as first char in string
		holder.textView.setText(value);

		return convertView;
	}

	@Override
	public T getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mItemResId, parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView
					.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		T item = getItem(position);

		String value;
		HashMap<String, String> map = (HashMap<String, String>) item;
		value = map.get(KEY_NAME);

		holder.textView.setText(value);

		return convertView;
	}

	private void init(Context context, ArrayList<T> items, int headerResId,
			int itemResId) {
		this.mItems = items;
		this.mHeaderResId = headerResId;
		this.mItemResId = itemResId;
		mInflater = LayoutInflater.from(context);
	}

	protected class HeaderViewHolder {
		public TextView textView;
	}

	protected class ViewHolder {
		public TextView textView;
	}
}