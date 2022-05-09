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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myprofile, container, false);
        mProfile = SaveSharedPreference.getMyProfile();

        if (mProfile.getImageURI().equals(" ")) {
            binding.ivMyProImage.setImageResource(R.mipmap.ic_person_base);
        } else {
            imageURI = SaveSharedPreference.getMyImage();
            Picasso.with(getContext()).load(imageURI).resize(1080, 1080).into(binding.ivMyProImage);
        }

        binding.tvMyName.setText(mProfile.getName());
        binding.tvMySex.setText(mProfile.getSex());
        binding.tvMyStateMsg.setText(mProfile.getStateMsg());
        binding.tvMyAge.setText(mProfile.getAge());
        binding.tvMyHeight.setText(mProfile.getHeight());
        binding.tvMyAddress.setText(mProfile.getAddress());
        binding.tvMyMajor.setText(mProfile.getMajor());
        binding.tvMyReligion.setText(mProfile.getReligion());
        binding.tvMyHobby.setText(mProfile.getHobby());
        binding.tvMyCollege.setText(mProfile.getCollege());
        binding.tvMyCircle.setText(mProfile.getCircle());
        binding.tvMyAbroadExperience.setText(mProfile.getAbroadExperience());
        binding.tvMyMilitaryStatus.setText(mProfile.getMilitaryStatus());

        binding.btnProfileModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ModifyProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.ivMyProImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageURI != null) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("SELECTED_IMAGE", imageURI);
                    startActivity(intent);