package com.project.network.ssugaeting.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityProfileModifyBinding;
import com.project.network.ssugaeting.databinding.ItemChatBinding;
import com.project.network.ssugaeting.ftp.FTPConnection;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import static com.project.network.ssugaeting.activity.MainActivity.DIRECTORY_PATH;
import static com.project.network.ssugaeting.activity.MainActivity.FTP_PATH;

public class ModifyProfileActivity extends AppCompatActivity {
    private static final char MODIFY_PROFILE = 'c';
    private static final int IMAGE_REQUEST_CODE = 1001;
    private static final String TAG = "ModifyProfileActivity";

    final String[] religionArray = {"미선택", "기독교", "천주교", "불교", "원불교", "무교"};
    final String[] hobbyArray = {"미선택", "독서", "운동", "게임", "영화", "음악",
            "악기연주", "드라마", "프로그래밍", "드라이브", "카페",
            "맛집탐방", "외국어", "사진", "봉사", "요리", "그림"};
    final String[] collegeArray = {"미선택", "인문대학", "자연과학대학", "법과대학", "사회과학대학",
            "경제통상대학", "경영대학", "공과대학", "IT대학"};
    final String[] circleArray = {"미선택", "학생회", "운동", "미술", "음악",
            "토론", "방송", "사진", "과학", "신문",
            "종교", "봉사", "학술", "친목", "외국어"};
    final String[] militaryStatusArray = {"미선택", "군필", "미필", "해당없음"};
    final String[] abroadExperienceArray = {"미선택", "영어권", "비영어권", "경험없음"};

    FTPConnection ftpConnection = new FTPConnection();
    ActivityProfileModifyBinding binding;
    Profile mProfile;
    int[] selectedPosArray = new int[6];
    String imageURI;
    ProgressDialog progressDialog;
    boolean ftpStatus;

    String mImgPath;
    String mImgTitle;
    String mImgOrient;
    File imgFile;
    String currentPath;

    public ModifyProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_modify);

        mProfile = SaveSharedPreference.getMyProfile();
        selectedPosArray = SaveSharedPreference.getSelectedPosArray();

        ArrayAdapter<String> religionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, religionArray);
        ArrayAdapter<String> hobbyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hobbyArray);
        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, collegeArray);
        ArrayAdapter<String> circleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circleArray);
        ArrayAdapter<String> abroadExperienceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, abroadExperienceArray);
        ArrayAdapter<String> militaryStatusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, militaryStatusArray);

        religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hobbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        circleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abroadExperienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        militaryStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.d("IMAGEURI", mProfile.getImageURI());
        // Set Profile Layout
        if (mProfile.getImageURI().equals(" "))
            binding.ivModifyProImage.setImageResource(R.mipmap.ic_person_base);
        else {
            imageURI = SaveSharedPreference.getMyImage();
            FtpDownloadProTask mFtpDownloadProTask = new FtpDownloadProTask(imageURI, this);
            mFtpDownloadProTask.execute();
        }
        binding.etModifyName.setText(mProfile.getName());
        binding.etModifyStateMsg.setText(mProfile.getStateMsg());
        binding.etModifyAge.setText(mProfile.getAge());
        binding.etModifyHeight.setText(mProfile.getHeight());
        binding.etModifyAddress.setText(mProfile.getAddress());
        binding.etModifyMajor.setText(mProfile.getMajor());

        binding.spModifyReligion.setAdapter(religionAdapter);
        binding.spModifyHobby.setAdapter(hobbyAdapter);
        binding.spModifyCollege.setAdapter(collegeAdapter);
        binding.spModifyCircle.setAdapter(circleAdapter);
        binding.spModifyAbroadExperience.setAdapter(abroadExperienceAdapter);
        binding.spModifyMilitaryStatus.setAdapter(militaryStatusAdapter);

        binding.spModifyReligion.setSelection(selectedPosArray[0]);
        binding.spModifyHobby.setSelection(selectedPosArray[1]);
        binding.spModifyCollege.setSelection(selectedPosArray[2]);
        binding.spModifyCircle.setSelection(selectedPosArray[3]);
        binding.spModifyAbroadExperience.setSelection(selectedPosArray[4]);
        binding.spModifyMilitaryStatus.setSelection(selectedPosArray[5]);

        binding.btnModifyComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile.setName(binding.etModifyName.getText().toString());
                mProfile.setStateMsg(binding.etModifyStateMsg.getText().toString());
                mProfile.setAge(binding.etModifyAge.getText().toString());
                mProfile.setHeight(binding.etModifyHeight.getText().toString());
                mProfile.setAddress(binding.etModifyAddress.getText().toString());
                mProfile.setMajor(binding.etModifyMajor.getText().toString());
                SaveSharedPreference.setMyProfile(mProfile);
                SaveSharedPreference.setSelectedPosArray(selectedPosArray);
                setSpace();
                // send modified Profile to server
                StringBuffer modifyProfileSb = new StringBuffer();
                modifyProfileSb.append(MODIFY_PROFILE).append("$");
                modifyProfileSb.append(mProfile.getId()).append("$");
                modifyProfileSb.append(mProfile.getPassword()).append("$");
                modifyProfileSb.append(mProfile.getEmail()).append("$");
                modifyProfileSb.append(mProfile.getName()).append("$");
                modifyProfileSb.append(mProfile.getSex()).append("$");
                modifyProfileSb.append(mProfile.getStateMsg()).append("$");
                modifyProfileSb.append(mProfile.getAge()).append("$");
                modifyProfileSb.append(mProfile.getHeight()).append("$");
                modifyProfileSb.append(mProfile.getAddress()).append("$");
                modifyProfileSb.append(mProfile.getHobby()).append("$");
                modifyProfileSb.append(mProfile.getCollege()).append("$");
                modifyProfileSb.append(mProfile.getMajor()).append("$");
                modifyProfileSb.append(mProfile.getImageURI()).append("$");
                modifyProfileSb.append(mProfile.getReligion()).append("$");
                modifyProfileSb.append(mProfile.getCircle()).append("$");
                modifyProfileSb.append(mProfile.getAbroadExperience()).append("$");
                modifyProfileSb.append(mProfile.getMilitaryStatus());
                ModifyProfileTask mModifyProfileTask = new ModifyProfileTask(modifyProfileSb.toString());
                mModifyProfileTask.execute();

                backToMain();
            }
        });

        binding.btnModifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        binding.ivModifyProImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "프로필 사진을 선택하세요."), IMAGE_REQUEST_CODE);
            }
        });

        binding.spModifyReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setReligion(religionArray[position]);
                selectedPosArray[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spModifyHobby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setHobby(hobbyArray[position]);
                selectedPosArray[1] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spModifyCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setCollege(collegeArray[position]);
                selectedPosArray[2] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spModifyCircle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setCircle(circleArray[position]);
                selectedPosArray[3] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spModifyAbroadExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setAbroadExperience(abroadExperienceArray[position]);
                selectedPosArray[4] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spModifyMilitaryStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProfile.setMilitaryStatus(militaryStatusArray[position]);
                selectedPosArray[5] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void backToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("position", 2);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePathURI;
            filePathURI = data.getData();
            if (filePathURI != null) {
                imageURI = filePathURI.toString();
                SaveSharedPreference.setMyImage(imageURI);
                // content://media/external/images/media/4080
                Log.d("content", imageURI);
                Picasso.with(this).load(imageURI).into(binding.ivModifyProImage);
                getImageNameToUri(filePathURI);
                imgFile = new File(mImgPath);
                mProfile.setImageURI(mImgPath);
                // upload to ftp
                if (!mProfile.getImageURI().equals(" ")) {
                    final Thread ftpThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            imgFile = new File(mImgPath);
                            Log.d("mImgPath", mImgPath);
                            ftpStatus = ftpConnection.ftpConnect();
                            if (ftpStatus)
                                Log.d(TAG, "FTP 연결 성공");
                            else
                                Log.d(TAG, "FTP 연결 실패");
                            ftpConnection.ftpChangeDirectory("profile/");
                            ftpConnection.ftpUploadFile(imgFile, mProfile.getId());
                            currentPath = ftpConnection.ftpGetDirectory();
                            currentPath += "/" + mProfile.getId() + ".jpg";
       