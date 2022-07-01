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

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setStateMsg(String stateMsg) {
        this.stateMsg = stateMsg;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public void setAbroadExperience(String abroadExperience) {
        this.abroadExperience = abroadExperience;
