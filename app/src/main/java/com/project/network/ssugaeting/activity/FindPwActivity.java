
package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityFindPwBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;

public class FindPwActivity extends AppCompatActivity {
    private static final int FIND_PASSWORD = 4;
    private static final int MODIFY_PASSWORD = 5;

    ActivityFindPwBinding binding;
    ProgressDialog progressDialog;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_pw);

        binding.btnFindPwConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = binding.etFindPwById.getText().toString();
                String email = binding.etFindPwByEmail.getText().toString();
                if (id.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer findPwSb = new StringBuffer();
                findPwSb.append(FIND_PASSWORD).append("$");
                findPwSb.append(id).append("$");
                findPwSb.append(email);
                FindPwTask mFindPwTask = new FindPwTask(findPwSb.toString());
                mFindPwTask.execute();
            }
        });

        binding.btnFindPwCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }