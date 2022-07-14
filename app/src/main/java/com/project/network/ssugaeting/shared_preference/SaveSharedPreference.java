
package com.project.network.ssugaeting.shared_preference;

import com.project.network.ssugaeting.item.Profile;

import java.util.ArrayList;

/**
 * Created by Jin on 2018-05-09.
 */

public class SaveSharedPreference {
    static Profile myProfile;
    static int[] selectedPosArray = new int[6];
    static ArrayList<String> connectionRequestedIdList = new ArrayList<>(100);
    static ArrayList<String> connectedIdList = new ArrayList<>(100);
    static String myImage;
    static String msgImage;

    public static Profile getMyProfile() {
        if (myProfile == null)
            myProfile = new Profile("a", "1", "sngsl@soongsil.com", "관리자", "남성", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ");
        return myProfile;
    }

    public static void setMyProfile(Profile myProfile) {
        SaveSharedPreference.myProfile = myProfile;
    }

    public static int[] getSelectedPosArray() {
        return selectedPosArray;
    }

    public static void setSelectedPosArray(int[] selectedPosArray) {
        SaveSharedPreference.selectedPosArray = selectedPosArray;
    }

    public static void removeConnectionRequestId(String oppId) {
        connectionRequestedIdList.remove(oppId);
    }

    public static void addConnectionRequestedId(String oppId) {
        connectionRequestedIdList.add(oppId);
    }

    public static ArrayList<String> getConnectionRequestedIdList() {
        return connectionRequestedIdList;
    }

    public static void addConnectedId(String oppId) {
        connectedIdList.add(oppId);
    }

    public static ArrayList<String> getConnectedIdList() {
        return connectedIdList;
    }

    public static String getMyImage() {
        return myImage;
    }

    public static void setMyImage(String img) {
        myImage=img;
    }

    public static String getMsgImage() {
        return msgImage;
    }

    public static void setMsgImage(String img) {
        msgImage=img;
    }
}