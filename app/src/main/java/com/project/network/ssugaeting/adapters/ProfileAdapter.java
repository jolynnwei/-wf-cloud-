
package com.project.network.ssugaeting.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.item_view.ProfileView;

import java.util.ArrayList;

/**
 * Created by Jin on 2018-04-08.
 */

public class ProfileAdapter extends BaseAdapter {
    private ArrayList<Profile> items=new ArrayList<Profile>();
    Context context;

    public ProfileAdapter(Context context) {
        this.context=context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileView mView= (ProfileView)convertView;

        if(convertView==null)
            mView=new ProfileView(context.getApplicationContext());

        Profile item=items.get(position);
        mView.setName(item.getName());
        mView.setStateMsg(item.getStateMsg());
        mView.setImageURL(item.getImageURI());

        return mView;
    }

    public void addItem(Profile item) { items.add(item); }

    public void clearItem() {items.clear(); }
}