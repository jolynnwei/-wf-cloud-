
package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityFindIdBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;

public class FindIdActivity extends AppCompatActivity {
    private static final int FIND_ID = 3;

    ActivityFindIdBinding binding;
    ProgressDialog progressDialog;
    boolean isCorrectEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id);

        binding.btnFindIdConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etFindIdByEmail.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer findIdSb = new StringBuffer();
                findIdSb.append(FIND_ID).append("$");
                findIdSb.append(email);
                FindIdTask mFindIdTask = new FindIdTask(findIdSb.toString());
                mFindIdTask.execute();

            }
        });

        binding.btnFindIdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class FindIdTask extends AsyncTask<Void, Void, String> {
        private String values;

        public FindIdTask(String values) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FindIdActivity.this);
            if (result.equals("UNCONNECTED")) {
                Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_LONG).show();
            } else if (result.equals("FAIL")) {
                builder.setTitle("실패");
                builder.setMessage("입력하신 정보와 일치하는 아이디가 존재하지 않습니다.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                isCorrectEmail = true;
                builder.setTitle("아이디 확인");
                int len = result.length();
                String temp = result.substring(0, (len + 1) / 2);
                for (int i = (len + 1) / 2; i < len; i++) {
                    temp = temp + "*";
                }
                builder.setMessage(temp);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                builder.show();
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(values);
            int idx = result.indexOf("&");
            result = result.substring(0, idx);
            return result;
        }
    }
}