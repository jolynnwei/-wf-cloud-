package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityLoginBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;

import java.util.StringTokenizer;

public class LoginActivity extends AppCompatActivity {
    private static final int ID_LOGIN = 2;

    ActivityLoginBinding binding;
    Profile mProfile;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        if (SaveSharedPreference.getMyProfile() == null) {
            // after connect server
        }

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.tvFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.tvFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPwActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new LoginClickListener());

    }

    private class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String id = binding.etInputId.getText().toString();
            if(id.equals("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
                return;
            }
            String password = binding.etInputPw.getText().toString();
            if(password.equals("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                return;
            }

            StringBuffer loginSb = new StringBuffer();
            loginSb.append(ID_LOGIN).append("$");
            loginSb.append(id).append("$");
            loginSb.append(password);
            LoginTask mLoginTask = new LoginTask(loginSb.toString());
            mLoginTask.execute();
        }
    }

    private void setProfileByMessage(String requestMessage) {
        StringTokenizer tokenizer = new StringTokenizer(requestMessage, "$");
        String id = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        String email = tokenizer.nextToken();
        String name = tokenizer.nextToken();
        String sex = tokenizer.nextToken();
        String stateMsg = tokenizer.nextToken();
        String age = tokenizer.nextToken();
        String height = tokenizer.nextToken();
        String address = tokenizer.nextToken();
        String hobby = tokenizer.nextToken();
        String college = tokenizer.nextToken();
        String major = tokenizer.nextToken();
        String imageURI = tokenizer.nextToken();
        String religion = tokenizer.nextToken();
        String circle = tokenizer.nextToken();
        String abroadExperience = tokenizer.nextToken();
        String militaryStatus = tokenizer.nextToken();
        mProfile = new Profile(id, password, email, name, sex, stateMsg, age, height,
                address, hobby, college, major, imageURI, religion, circle, abroadExperience, militaryStatus);
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {
        private String values;

        public LoginTask(String values) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("오류");
                builder.setMessage("존재하지 않는 아이디이거나 비밀번호가 일치하지 않습니다.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                setProfileByMessage(result);
                SaveSharedPreference.setMyProfile(mProfile);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class)