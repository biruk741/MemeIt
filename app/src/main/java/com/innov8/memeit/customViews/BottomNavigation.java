package com.innov8.memeit.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.innov8.memeit.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;

/**
 * Created by Jv on 7/7/2018.
 */


public class BottomNavigation extends LinearLayout {
    int menu_resource;
    int skip_tint;
    int item_padding;
    boolean showTitle;
    int size;

    int selected_tint;
    int deselected_tint;

    LayoutParams layoutParams;

    int selectedIndex;
    int c;

    Menu menu;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    public BottomNavigation(Context context) {
        super(context);
    }

    @SuppressLint("RestrictedApi")
    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigation, 0, 0);
        try {
            menu_resource = a.getResourceId(R.styleable.BottomNavigation_menu, 0);
            skip_tint = a.getInteger(R.styleable.BottomNavigation_skip_tint, -1);
            selected_tint = a.getColor(R.styleable.BottomNavigation_selected_tint, Color.RED);
            deselected_tint = a.getColor(R.styleable.BottomNavigation_deselected_tint, Color.GRAY);
            item_padding = (int) a.getDimension(R.styleable.BottomNavigation_item_padding, 0f);
        } finally {
            a.recycle();
        }
        layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);

        if (menu_resource == 0) return;
        MenuInflater inflator = new MenuInflater(context);
        menu = new MenuBuilder(context);
        inflator.inflate(menu_resource, menu);
        init();
        select(0,false);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int no = Integer.parseInt(String.valueOf(view.getTag()));
                select(no,true);

            }
        };

        for (int i = 0; i < menu.size(); i++) {
            final MenuItem item = menu.getItem(i);
            ImageView imgv = new ImageView(getContext());
            imgv.setImageDrawable(item.getIcon());
            imgv.setOnClickListener(listener);
            imgv.setTag(i);
            imgv.setPadding(0, item_padding, 0, item_padding);
            addView(imgv, layoutParams);
        }

    }

    public void select(int x, boolean fire) {
        ((ImageView) getChildAt(selectedIndex)).setImageResource(getDrawableForItem(false));
        selectedIndex = x;
        if(!isNotTintable(x))clearTints(c);
        setTinted(x);
        if (fire)
            onNavigationItemSelectedListener.onNavigationItemSelected(menu.getItem(x));
    }

    private void setTinted(int x) {
        if (isNotTintable(x)) return;
        ImageView iv = (ImageView) getChildAt(x);
        iv.setColorFilter(selected_tint, PorterDuff.Mode.SRC_IN);
        iv.setImageResource(getDrawableForItem(true));
    }
    public int getDrawableForItem(boolean fill){
        int a;
        switch (getSelectedIndex()){
            case 0:
                a=getIdFromString(!fill ? "home" : "home_fill");
                break;
            case 1:
                a=getIdFromString(!fill ? "trending" : "trending_fill");
                break;
            case 2:
                a=getIdFromString(!fill ? "create" : "create");
                break;
            case 3:
                a=getIdFromString(!fill ? "favorites" : "favorites_fill");
                break;
            case 4:
                a=getIdFromString(!fill ? "profile" : "profile_fill");
                break;
                default:a=getIdFromString(!fill ? "home" : "home_fill");
        }
        return a;
    }
    private String getStringFromId(int id){
        return getResources().getResourceName(id);
    }
    private int getIdFromString(String s){
        return getResources().getIdentifier(s,"drawable",getContext().getPackageName());
    }

    private void clearTints(int c) {
        for (int i = 0; i < getChildCount(); i++) {
            if (isNotTintable(i)) continue;
            ImageView iv = (ImageView) getChildAt(i);
            iv.setColorFilter(deselected_tint, PorterDuff.Mode.SRC_IN);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private boolean isNotTintable(int index) {
        return skip_tint == -1 || index == skip_tint;
    }

    public void setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        this.onNavigationItemSelectedListener = onNavigationItemSelectedListener;
    }
}
