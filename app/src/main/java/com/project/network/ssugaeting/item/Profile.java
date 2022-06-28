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

    public Profile(String id, String password, String email, String name, String sex, String stateMsg, String age, String height, String address, String hobby, String college, String major, String imageURI, String religion, String circle, String abroadExperience, String militaryStatus) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
        this.sex = sex;
        this.stateMsg = stateMsg;
        this.age = age;
        this.height = height;
        this.address = address;
        this.hobby = hobby;
        this.college = college;
        this.major = major;
        this.imageURI = imageURI;
        this.religion = religion;
        this.circle = circle;
        this.abroadExperience = abroadExperience;
        this.militaryStatus = militaryStatus;
    }


    protected Profile(Parcel in) {
        id = in.readString();
        password = in.readString();
        email = in.readString();
        name = in.readString();
        sex = in.readString();
        stateMsg = in.readString();
        age = in.readString();
        height = in.readString();
        address = in.readString();
        hobby = in.readString();
        college = in.readString();
        major = in.readString();
        imageURI = in.readString();
        religion = in.readString();
        circle = in.readString();
        abroadExperience = in.readString();
        militaryStatus = in.readString();
    }

    public static final Creator<Profile> CREATOR = n