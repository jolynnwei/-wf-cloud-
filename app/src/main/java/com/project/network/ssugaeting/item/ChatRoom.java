package com.project.network.ssugaeting.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Jin on 2018-05-30.
 */

public class ChatRoom implements Parcelable {
    private ArrayList<Chat> chatList;
    private Profile profile;
    private int unCheckMsgCnt;

    public ChatRoom(ArrayList<Chat> chatList, Profile profile, int unCheckMsgCnt) {
        this.chatList = chatList;
        this.profile = profile;
        this.unCheckMsgCnt = unCheckMsgCnt;
    }

    protected ChatRoom(Parcel in) {
        chatList = in.createTypedArrayList(Chat.CREATOR);
        profile = in.readParcelable(Profile.class.getClassLoader());
        unCheckMsgCnt = in.readInt();
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(chatList);
        dest.writeParcelable(profile, flags);
        dest.writeInt(unCheckMsgCnt);
    }

    public ArrayList<Chat> getChatList() {
        return chatList;
    }

    public void setChatList(ArrayList<Chat> chatList) {
        this.chatList = chatList;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
 