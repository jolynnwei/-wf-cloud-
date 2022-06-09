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
        chatList = in.createTypedArrayList(Chat.CREATOR)