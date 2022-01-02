
package com.project.network.ssugaeting.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityProfileBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int CONNECT_REQUEST = 8;

    ActivityProfileBinding binding;
    String imageURI;
    String name;
    ProgressDialog progressDialog;
    Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);


        // From ProfileFragment
        final Intent intent = ProfileActivity.this.getIntent();
        final Profile sProfile = intent.getParcelableExtra("SELECTED_PROFILE");
        mProfile = SaveSharedPreference.getMyProfile();

        // Set Profile Layout
        if (sProfile.getImageURI().equals(" "))
            binding.ivProfileImage.setImageResource(R.mipmap.ic_person_base);
        else {
            imageURI = sProfile.getImageURI();
            Log.d(TAG, imageURI);
            binding.ivProfileImage.setImageURI(Uri.parse(imageURI));
        }

       name=sProfile.getName();
        if(name.equals("곧미인")) {
            binding.ivProfileImage.setImageResource(R.drawable.a1);
        } else if(name.equals("김윤아")) {
            binding.ivProfileImage.setImageResource(R.drawable.a2);
        } else if(name.equals("달빛천사")) {
            binding.ivProfileImage.setImageResource(R.drawable.a3);
        } else if(name.equals("민지민지")) {
            binding.ivProfileImage.setImageResource(R.drawable.a4);
        } else if(name.equals("배윤경")) {
            binding.ivProfileImage.setImageResource(R.drawable.a0);
        } else if(name.equals("윤혜쿵해쪄")) {
            binding.ivProfileImage.setImageResource(R.drawable.a5);
        } else if(name.equals("장미 한송이")) {
            binding.ivProfileImage.setImageResource(R.drawable.a6);
        } else if(name.equals("지유니쿵")) {
            binding.ivProfileImage.setImageResource(R.drawable.a7);
        }

        binding.tvProfileName.setText(sProfile.getName());
        binding.tvProfileSex.setText(sProfile.getSex());
        binding.tvProfileStateMsg.setText(sProfile.getStateMsg());
        binding.tvProfileAge.setText(sProfile.getAge());
        binding.tvProfileHeight.setText(sProfile.getHeight());
        binding.tvProfileAddress.setText(sProfile.getAddress());
        binding.tvProfileMajor.setText(sProfile.getMajor());
        binding.tvProfileReligion.setText(sProfile.getReligion());
        binding.tvProfileHobby.setText(sProfile.getHobby());
        binding.tvProfileCollege.setText(sProfile.getCollege());
        binding.tvProfileCircle.setText(sProfile.getCircle());
        binding.tvProfileAbroadExperience.setText(sProfile.getAbroadExperience());
        binding.tvProfileMilitaryStatus.setText(sProfile.getMilitaryStatus());

        binding.btnConnectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.addConnectionRequestedId(sProfile.getId());
                String myId = mProfile.getId();
                StringBuffer connReqSb = new StringBuffer();
                connReqSb.append(CONNECT_REQUEST);
                connReqSb.append("$");
                connReqSb.append(myId);
                connReqSb.append("$");
                connReqSb.append(sProfile.getId());
                ConnectRequestTask mConnectRequestTask = new ConnectRequestTask(connReqSb.toString());
                mConnectRequestTask.execute();
            }
        });

        binding.btnProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("SELECTED_IMAGE", name);
                startActivity(intent);
            }
        });
    }

    private class ConnectRequestTask extends AsyncTask<Void, Void, String> {
        private String values;

        public ConnectRequestTask(String values) {
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
                Toast.makeText(getApplicationContext(), "연결 요청을 보냈습니다.", Toast.LENGTH_LONG).show();
            } else if (result.equals("FAIL")) {
                Toast.makeText(getApplicationContext(), "연결 요청 실패", Toast.LENGTH_LONG).show();
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