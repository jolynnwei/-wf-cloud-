package com.project.network.ssugaeting.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jin on 2018-04-07.
 */

public class Profile implements Parcelable {
    // Essential Information
    private String id;
    private String password;
    private String email;
    private String name;
    private String sex;
    // Additional Information
    private String stateMsg;
    private String age;
    private String height;
    private String address;
    private String hobby;
    private String college;
