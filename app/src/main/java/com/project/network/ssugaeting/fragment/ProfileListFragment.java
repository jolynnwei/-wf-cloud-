package com.project.network.ssugaeting.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.activity.ProfileActivity;
import com.project.network.ssugaeting.adapters.ProfileAdapter;
import com.project.network.ssugaeting.databinding.FragmentProfileListBinding;
import com.project.network.ssugaeting.item.Profile;

import java.util.ArrayList;

public class ProfileListFragment extends Fragment {

    static ProfileAdapter profileAdapter;
    FragmentProfileListBinding binding;

    ArrayList<Profile> mProfileList = new ArrayList<>(100);

    public ProfileListFragment() {

    }

    public static ProfileListFragment newInstance(ArrayList<Profile> filteredProfileList) {
        ProfileListFragment mProfileListFragment = new ProfileListFragment();
        Bundle mBundle = new Bundle();
        mBundle.putParcelableArrayList("FILTERED_PROFILES", filteredProfileList);
        mProfileListFragment.setArguments(mBundle);
        return mProfileListFragment;
    }

    public static void clearProfileList() {
        profileAdapter.clearItem();
    }

    @Override
    public void onCreate(Bundle save