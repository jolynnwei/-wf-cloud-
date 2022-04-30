package com.project.network.ssugaeting.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.activity.ImageActivity;
import com.project.network.ssugaeting.activity.ModifyProfileActivity;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;
import com.project.network.ssugaeting.databinding.FragmentMyprofileBinding;
import com.project.network.ssugaeting.item.Profile;
import com.squareup.picasso.Picasso;

public class MyProfileFragment extends Fragment {

    FragmentMyprofileBinding binding;
    String imageURI;
    Profile mProfile;

    public MyProfileFragment() {
    }

    public static MyProfileFragment newInstance() {
        return new MyProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflate