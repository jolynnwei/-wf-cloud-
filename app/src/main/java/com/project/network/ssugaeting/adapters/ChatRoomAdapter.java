
package com.project.network.ssugaeting.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.ChatRoom;
import com.project.network.ssugaeting.item_view.ChatRoomView;

import java.util.ArrayList;

/**
 * Created by Jin on 2018-04-08.
 */

public class ChatRoomAdapter extends BaseAdapter {
    private ArrayList<ChatRoom> items = new ArrayList<>();
    Context context;

    public ChatRoomAdapter(Context context) {
        this.context = context;
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
        ChatRoomView mView = (ChatRoomView) convertView;

        if (convertView == null)
            mView = new ChatRoomView(context.getApplicationContext());

        ChatRoom item = items.get(position);

        mView.setUserName(item.getProfile().getName());
        if(!item.getChatList().isEmpty()) {
            int last = item.getChatList().size() - 1;
            mView.setUserMsg(item.getChatList().get(last).getSendMsg());
            mView.setMsgTime(item.getChatList().get(last).getMsgTime());
        }
        mView.setMsgCount(item.getUnCheckMsgCnt());
        mView.setUserImageURL(item.getProfile().getImageURI());

        return mView;
    }

    public void addItem(ChatRoom item) {
        items.add(item);
    }

    public void clearItem() {
        items.clear();
    }
}