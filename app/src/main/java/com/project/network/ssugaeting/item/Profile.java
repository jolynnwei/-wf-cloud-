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
    private String major;
    private String imageURI;
    private String religion;
    private String circle;
    private String abroadExperience;
    private String militaryStatus;

    // Constructor of Essential Information
    public Profile(String id, String password, String email, String name, String sex) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.sex = sex;
    }

    public Profile(String id, String password, String email, String