package com.project.network.ssugaeting.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Jin on 2018-04-08.
 */

public class Chat implements Parcelable, Serializable {
    private String sendMsg;
    private String sendImageURI;
    private String msgTime;
    private int msgTurn;

    public Chat(