
package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityFilterBinding;
import com.project.network.ssugaeting.ftp.FTPConnection;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.project.network.ssugaeting.activity.MainActivity.DIRECTORY_PATH;
import static com.project.network.ssugaeting.activity.MainActivity.FTP_PATH;

public class FilterActivity extends AppCompatActivity {
    private static final String TAG = "FilterActivity";
    private static final int FILTER_INFO = 6;

    final String[] religionArray = {"상관없음", "기독교", "천주교", "불교", "원불교", "무교"};
    final String[] hobbyArray = {"상관없음", "독서", "운동", "게임", "영화", "음악",
            "악기연주", "드라마", "프로그래밍", "드라이브", "카페",
            "맛집탐방", "외국어", "사진", "봉사", "요리", "그림"};
    final String[] collegeArray = {"상관없음", "인문대학", "자연과학대학", "법과대학", "사회과학대학",
            "경제통상대학", "경영대학", "공과대학", "IT대학"};
    final String[] circleArray = {"상관없음", "학생회", "운동", "미술", "음악",
            "토론", "방송", "사진", "과학", "신문",
            "종교", "봉사", "학술", "친목", "외국어"};
    final String[] militaryStatusArray = {"상관없음", "군필", "미필"};
    final String[] abroadExperienceArray = {"상관없음", "영어권", "비영어권"};

    ActivityFilterBinding binding;
    ProgressDialog progressDialog;

    FTPConnection ftpConnection = new FTPConnection();
    String ageFrom, ageTo;
    String heightFrom, heightTo;
    String fltReligion, fltHobby, fltCollege, fltCircle, fltAbrdExp, fltMltrStat;
    Profile mProfile;
    ArrayList<Profile> filteredProfileList = new ArrayList<>(100);
    boolean ftpStatus;
    String currentPath;
    String oppSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

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

        binding.spFilterReligion.setAdapter(religionAdapter);
        binding.spFilterHobby.setAdapter(hobbyAdapter);
        binding.spFilterCollege.setAdapter(collegeAdapter);
        binding.spFilterCircle.setAdapter(circleAdapter);
        binding.spFilterAbroadExperience.setAdapter(abroadExperienceAdapter);
        binding.spFilterMilitaryStatus.setAdapter(militaryStatusAdapter);

        binding.btnSearchByFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile = SaveSharedPreference.getMyProfile();
                ageFrom = binding.etFilterAgeFrom.getText().toString();
                if (ageFrom.equals("")) ageFrom = "none";
                ageTo = binding.etFilterAgeTo.getText().toString();
                if (ageTo.equals("")) ageTo = "none";
                heightFrom = binding.etFilterHeightFrom.getText().toString();
                if (heightFrom.equals("")) heightFrom = "none";
                heightTo = binding.etFilterHeightTo.getText().toString();
                if (heightTo.equals("")) heightTo = "none";
                setNone();
                StringBuffer filterSb = new StringBuffer();
                filterSb.append(FILTER_INFO).append("$");
                if (mProfile.getSex().equals("남성")) oppSex = "여성";
                else if (mProfile.getSex().equals("여성")) oppSex = "남성";
                filterSb.append(oppSex).append("$");
                filterSb.append(ageFrom).append("$");
                filterSb.append(ageTo).append("$");
                filterSb.append(heightFrom).append("$");
                filterSb.append(heightTo).append("$");
                filterSb.append(fltReligion).append("$");
                filterSb.append(fltHobby).append("$");
                filterSb.append(fltCollege).append("$");
                filterSb.append(fltCircle).append("$");
                filterSb.append(fltAbrdExp).append("$");
                filterSb.append(fltMltrStat);
                final Thread ftpThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ftpStatus = ftpConnection.ftpConnect();
                        if (ftpStatus)
                            Log.d(TAG, "FTP 연결 성공");
                        else
                            Log.d(TAG, "FTP 연결 실패");
                        ftpConnection.ftpChangeDirectory("profile/");
                        for (int i = 0; i < filteredProfileList.size(); i++) {
                            String oppId = filteredProfileList.get(i).getId();
                            currentPath = DIRECTORY_PATH + "/profile";
                            currentPath += "/" + oppId + ".jpg";
                            ftpConnection.ftpDownloadFile(FTP_PATH + "/profile/" + oppId + ".jpg", currentPath);
                            filteredProfileList.get(i).setImageURI(currentPath);
                        }
                        ftpConnection.ftpDisconnect();
                    }
                });
                ftpThread.start();

                FilterInfoTask mFilterInfoTask = new FilterInfoTask(filterSb.toString());
                mFilterInfoTask.execute();
            }
        });

        binding.btnFilterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.spFilterReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltReligion = religionArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltReligion = religionArray[0];
            }
        });

        binding.spFilterHobby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltHobby = hobbyArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltHobby = hobbyArray[0];
            }
        });

        binding.spFilterCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltCollege = collegeArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltCollege = collegeArray[0];
            }
        });

        binding.spFilterCircle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltCircle = circleArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltCircle = circleArray[0];
            }
        });

        binding.spFilterAbroadExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltAbrdExp = abroadExperienceArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltAbrdExp = abroadExperienceArray[0];
            }
        });

        binding.spFilterMilitaryStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fltMltrStat = militaryStatusArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fltMltrStat = militaryStatusArray[0];

            }
        });
    }

    private class FilterInfoTask extends AsyncTask<Void, Void, String> {
        private String values;

        public FilterInfoTask(String values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getLayoutInflater().getContext(), "Connecting Server", "Please wait...", false, false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equals("FAIL")) {
                Toast.makeText(getApplicationContext(), "해당하는 상대방이 존재하지 않습니다,", Toast.LENGTH_LONG).show();
                return;
            }
            StringTokenizer profileTokenizer = new StringTokenizer(result, "|");
            while (profileTokenizer.hasMoreTokens()) {
                String proInfo = profileTokenizer.nextToken();
                StringTokenizer infoTokenizer = new StringTokenizer(proInfo, "$");
                String id = infoTokenizer.nextToken();
                String password = infoTokenizer.nextToken();
                String email = infoTokenizer.nextToken();
                String name = infoTokenizer.nextToken();
                String sex = infoTokenizer.nextToken();
                String stateMsg = infoTokenizer.nextToken();
                String age = infoTokenizer.nextToken();
                String height = infoTokenizer.nextToken();
                String address = infoTokenizer.nextToken();
                String hobby = infoTokenizer.nextToken();
                String college = infoTokenizer.nextToken();
                String major = infoTokenizer.nextToken();
                String imageURI = infoTokenizer.nextToken();
                String religion = infoTokenizer.nextToken();
                String circle = infoTokenizer.nextToken();
                String abroadExperience = infoTokenizer.nextToken();
                String militaryStatus = infoTokenizer.nextToken();
                Profile fltProfile = new Profile(id, password, email, name, sex, stateMsg, age, height,
                        address, hobby, college, major, imageURI, religion, circle, abroadExperience, militaryStatus);
                filteredProfileList.add(fltProfile);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putParcelableArrayListExtra("FILTERED_PROFILES", filteredProfileList);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(values);
            Log.d("result", result);
            int idx = result.indexOf("&");
            result = result.substring(0, idx);
            return result;
        }
    }

    private void setNone() {
        if (fltReligion.equals("상관없음")) fltReligion = "none";
        if (fltHobby.equals("상관없음")) fltHobby = "none";
        if (fltCollege.equals("상관없음")) fltCollege = "none";
        if (fltCircle.equals("상관없음")) fltCircle = "none";
        if (fltAbrdExp.equals("상관없음")) fltAbrdExp = "none";
        if (fltMltrStat.equals("상관없음")) fltMltrStat = "none";
    }
}