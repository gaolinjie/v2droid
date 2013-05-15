
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

public class NavigationWidget extends LinearLayout {
    private final ListView list;

    public NavigationWidget(Context context) {
        this(context, null);
    }

    public NavigationWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.inflate(context, R.layout.navigation_widget, this, true);
        list = (ListView) findViewById(R.id.navigationListView);
    }

    public ListView getListView() {
        return list;
    }

    public void init(ListAdapter adapter,
            OnItemClickListener onItemClickListener, int theme, int page) {
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClickListener);
        list.performItemClick(null, page, 0);
        int themePicker = 0;
        if (ThemeManager.isDark(theme)) {
            themePicker = R.id.themePickerDark;
        }
        ((NavigationItem) findViewById(themePicker))
                .setSelectionHandlerColorResource(R.color.holo_green_light);
    }
}
