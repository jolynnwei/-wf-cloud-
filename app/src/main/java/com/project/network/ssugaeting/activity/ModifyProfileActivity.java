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

    final String[] religionArray = {"미선택", "기독교", "천주교", "불교",