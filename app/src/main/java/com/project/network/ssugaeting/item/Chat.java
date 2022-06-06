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

    public Chat(String sendMsg, String sendImageURI, String msgTime, int msgTurn) {
        this.sendMsg = sendMsg;
        this.sendImageURI = sendImageURI;
        this.msgTime = msgTime;
        this.msgTurn = msgTurn;
    }

    protected Chat(Parcel in) {
        sendMsg = in.readString();
        sendImageURI = in.readString();
        msgTime = in.readString();
        msgTurn = in.readInt();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int describeCon