
package com.project.network.ssugaeting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityMainBinding;
import com.project.network.ssugaeting.fragment.ChatRoomListFragment;
import com.project.network.ssugaeting.fragment.MyProfileFragment;
import com.project.network.ssugaeting.fragment.ProfileListFragment;
import com.project.network.ssugaeting.item.Profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "MainActivity";
    public static final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ssugaeting";
    public static final String FTP_PATH = "/home/testuser";
    // /home/testuser
    // /storage/emulated/0

    ActivityMainBinding binding;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};//권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101;//권한 동의 여부 문의 후 callback함수에 쓰일 변수

    private SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<Profile> filteredProfileList = new ArrayList<>(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (Build.VERSION.SDK_INT > 22) {
            checkPermissions();
        }

        filteredProfileList = getIntent().getParcelableArrayListExtra("FILTERED_PROFILES");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        binding.viewPager.setAdapter(mSectionsPagerAdapter);
        binding.viewPager.addOnPageChangeListener(this);

        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.setSelectedTabIndicatorColor(Color.BLACK);
        binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_black_24dp);
        binding.tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble_grey_24dp);
        binding.tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_grey_24dp);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileListFragment.clearProfileList();
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        int position = getIntent().getIntExtra("position", 0);
        binding.viewPager.setCurrentItem(position);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProfileListFragment.newInstance(filteredProfileList);
                case 1:
                    return ChatRoomListFragment.newInstance();
                case 2:
                    return MyProfileFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_black_24dp);
                binding.tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble_grey_24dp);
                binding.tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_grey_24dp);
                binding.fab.setVisibility(View.VISIBLE);
                break;
            case 1:
                binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_grey_24dp);
                binding.tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble_black_24dp);
                binding.tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_grey_24dp);
                binding.fab.setVisibility(View.INVISIBLE);
                break;
            case 2:
                binding.tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_grey_24dp);
                binding.tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble_grey_24dp);
                binding.tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_black_24dp);
                binding.fab.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);//현재 컨텍스트가 pm 권한을 가졌는지 확인
            if (result != PackageManager.PERMISSION_GRANTED) {//사용자가 해당 권한을 가지고 있지 않을 경우
                permissionList.add(pm);//리스트에 해당 권한명을 추가한다
            }
        }
        if (!permissionList.isEmpty()) {//권한이 추가되었으면 해당 리스트가 empty가 아니므로, request 즉 권한을 요청한다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    //권한 획득에 동의하지 않았을 경우, 아래 메시지를 띄우며 해당 액티비티를 종료.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

}

