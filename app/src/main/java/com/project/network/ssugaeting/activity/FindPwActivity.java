
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
        });
    }

    private class FindPwTask extends AsyncTask<Void, Void, String> {
        private String values;

        public FindPwTask(String values) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FindPwActivity.this);
            if (result.equals("UNCONNECTED")) {
                Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_LONG).show();
            } else if (result.equals("OK")) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.change_pw, null);
                builder.setView(view);

                final EditText newPwEditText = (EditText) view.findViewById(R.id.et_newPw);
                final EditText newPwCheckEditText = (EditText) view.findViewById(R.id.et_newPwCheck);
                final Button changePwConfirm = (Button) view.findViewById(R.id.btn_changePwConfirm);
                final Button changePwCancel = (Button) view.findViewById(R.id.btn_changePwCancel);

                final AlertDialog dialog = builder.create();
                changePwConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newPw = newPwEditText.getText().toString();
                        final String newPwCheck = newPwCheckEditText.getText().toString();
                        if (newPw.equals(newPwCheck)) {
                            StringBuffer modifyPwSb = new StringBuffer();
                            modifyPwSb.append(MODIFY_PASSWORD).append("$");
                            modifyPwSb.append(id).append("$");
                            modifyPwSb.append(newPw);
                            ModifyPwTask mModifyPwTask=new ModifyPwTask(modifyPwSb.toString());
                            mModifyPwTask.execute();
                            dialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                changePwCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();
            } else if (result.equals("FAIL")) {
                builder.setTitle("실패");
                builder.setMessage("입력하신 정보와 일치하는 회원정보가 존재하지 않습니다.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                Toast.makeText(getApplicationContext(), "전달된 데이터가 없습니다.", Toast.LENGTH_LONG).show();
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

    private class ModifyPwTask extends AsyncTask<Void, Void, String> {
        private String values;

        public ModifyPwTask(String values) {
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
            if (result.equals("UNCONNECTED")) {
                Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_LONG).show();
            } else if (result.equals("OK")) {
                Toast.makeText(getApplicationContext(), "비밀번호가 수정되었습니다.", Toast.LENGTH_LONG).show();
            } else if (result.equals("FAIL")) {
                Toast.makeText(getApplicationContext(), "비밀번호 변경 실패", Toast.LENGTH_LONG).show();;
            } else {
                Toast.makeText(getApplicationContext(), "전달된 데이터가 없습니다.", Toast.LENGTH_LONG).show();
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