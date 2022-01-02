
package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityRegisterBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.mail.SendMail;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final int CHECK_ID = 0;
    private static final int REGISTER_CLIENT = 1;

    ActivityRegisterBinding binding;
    boolean isCorrectId, isCorrectPw;
    boolean isCorrectEmail;
    String inputCertNumber;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        isCorrectId = false;
        isCorrectPw = false;
        isCorrectEmail = false;

        binding.btnCheckId.setOnClickListener(new IdCheckClickListener());
        binding.etRegPwCheck.setOnFocusChangeListener(new PwCheckFocusChangeListener());
        binding.btnCertEmail.setOnClickListener(new CertificationEmailClickListener());
        binding.btnRegConfirm.setOnClickListener(new RegisterClickListener());
        binding.btnRegCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.rbRegMale.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }

    private class IdCheckClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String id = binding.etRegId.getText().toString();
            if (id.equals("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuffer idSb=new StringBuffer();
            idSb.append(CHECK_ID);
            idSb.append("$");
            idSb.append(id);
            CheckIdTask mCheckIdTask = new CheckIdTask(idSb.toString());
            mCheckIdTask.execute();
        }
    }


    private class PwCheckFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String pw = binding.etRegPw.getText().toString();
            if (pw.equals("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String pwCheck = binding.etRegPwCheck.getText().toString();
            if (pw.equals(pwCheck)) {
                isCorrectPw = true;
                binding.tvPwCheck.setText("비밀번호가 일치합니다");
                binding.tvPwCheck.setTextColor(Color.GREEN);
            } else {
                binding.tvPwCheck.setText("비밀번호가 일치하지 않습니다.");
                binding.tvPwCheck.setTextColor(Color.RED);
                binding.tvPwCheck.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CertificationEmailClickListener implements View.OnClickListener {