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

    public ChatRoom(A