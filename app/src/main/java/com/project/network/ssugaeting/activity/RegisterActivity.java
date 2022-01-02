
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
        @Override
        public void onClick(View v) {
            final String certEmail = binding.etRegEmail.getText().toString();
            final String emailSubject = "슈개팅 이메일 인증번호";
            final int certNumber = (int) (Math.random() * 9000) + 1000;
            final String emailMessage = "인증번호는 [" + certNumber + "] 입니다.";

            if (certEmail.equals("")) {
                Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
                return;
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            final LayoutInflater inflater = getLayoutInflater();
            final View view = inflater.inflate(R.layout.certification_email, null);
            builder.setView(view);

            SendMail mSendMail = new SendMail(inflater.getContext(), certEmail, emailSubject, emailMessage);
            mSendMail.execute();

            final AlertDialog dialog = builder.create();

            final EditText certNumberEditText = (EditText) view.findViewById(R.id.et_certNumber);
            final Button certConfirmButton = (Button) view.findViewById(R.id.btn_certConfirm);
            final Button certCancelButton = (Button) view.findViewById(R.id.btn_certCancel);

            certConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputCertNumber = certNumberEditText.getText().toString();
                    if (inputCertNumber.compareTo(String.valueOf(certNumber)) == 0) {
                        isCorrectEmail = true;
                        Toast.makeText(getApplicationContext(), "인증 성공", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_LONG).show();
                    }
                }
            });

            certCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private class RegisterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isCorrectId)
                Toast.makeText(getApplication(), "아이디를 확인해주세요.", Toast.LENGTH_LONG).show();
            else if (!isCorrectPw)
                Toast.makeText(getApplication(), "비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
            else if (!isCorrectEmail)
                Toast.makeText(getApplication(), "이메일 인증을 해주세요.", Toast.LENGTH_LONG).show();
            else {
                String regId = binding.etRegId.getText().toString();
                String regPw = binding.etRegPw.getText().toString();
                String regName = binding.etRegName.getText().toString();
                String regEmail = binding.etRegEmail.getText().toString();
                String regSex;
                if (binding.rbRegMale.isChecked())
                    regSex = "남성";
                else if (binding.rbRegFemale.isChecked())
                    regSex = "여성";
                else {
                    Toast.makeText(getApplication(), "성별을 선택해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuffer regSb = new StringBuffer();
                regSb.append(REGISTER_CLIENT);
                regSb.append("$");
                regSb.append(regId);
                regSb.append("$");
                regSb.append(regPw);
                regSb.append("$");
                regSb.append(regEmail);
                regSb.append("$");
                regSb.append(regName);
                regSb.append("$");
                regSb.append(regSex);

                RegisterCheckTask mRegisterCheckTask = new RegisterCheckTask(regSb.toString());
                mRegisterCheckTask.execute();
            }

        }
    }

    private class CheckIdTask extends AsyncTask<Void, Void, String> {
        private String values;

        public CheckIdTask(String values) {
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
            if (result != null) {
                if (result.equals("UNCONNECTED")) {
                    Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_LONG).show();
                } else if (result.equals("OK")) {
                    isCorrectId = true;
                    binding.tvIdCheck.setText("사용가능한 아이디입니다.");
                    binding.tvIdCheck.setTextColor(Color.GREEN);
                    binding.tvIdCheck.setVisibility(View.VISIBLE);
                } else if (result.equals("FAIL")) {
                    binding.tvIdCheck.setText("중복된 아이디입니다.");
                    binding.tvIdCheck.setTextColor(Color.RED);
                    binding.tvIdCheck.setVisibility(View.VISIBLE);
                }
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
            if (result != null) {
                int idx = result.indexOf("&");
                result = result.substring(0, idx);
            }
            return result;
        }
    }

    private class RegisterCheckTask extends AsyncTask<Void, Void, String> {
        private String values;

        public RegisterCheckTask(String values) {
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
            if (result != null) {
                switch (result) {
                    case "UNCONNECTED":
                        Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_LONG).show();
                        break;
                    case "OK":
                        Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case "FAIL":
                        Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
                        break;
                }
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
            if (result != null) {
                int idx = result.indexOf("&");
                result = result.substring(0, idx);
            }
            return result;
        }
    }

}