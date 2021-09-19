
package com.project.network.ssugaeting.activity;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.adapters.ChatAdapter;
import com.project.network.ssugaeting.databinding.ActivityChatroomBinding;
import com.project.network.ssugaeting.ftp.FTPConnection;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.ChatRoom;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import static com.project.network.ssugaeting.activity.MainActivity.DIRECTORY_PATH;

public class ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = "ChatRoomActivity";
    private static final char SEND_MSG = 'a';
    private static final char SEND_IMAGE = 'b';
    private static final char RECEIVE_MSG = 'e';
    private static final String[] DAY_OF_WEEK = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

    public static final String CONNECT_REQUEST_BY_ME = "상대방이 연결요청 처리중입니다...";
    public static final String CONNECT_REQUEST_BY_OPPONENT = "CONNECT_REQUEST";

    public static final int MY_TURN = 1;
    public static final int OPPONENT_TURN = 2;
    public static final int DATE_TURN = 3;
    private static final int IMAGE_REQUEST_CODE = 1001;

    private static final float ENABLED_ALPHA = (float) 1.0;
    private static final float DISABLED_ALPHA = (float) 0.2;

    private ArrayList<String> connReqIdList;

    private FTPConnection ftpConnection = new FTPConnection();
    private Profile mProfile;
    private ChatRoom chatRoom;
    private ChatAdapter chatAdapter;
    private File chatsFile;
    private boolean isExistReqId;
    private boolean ftpStatus;

    String mImgPath;
    String mImgTitle;
    String mImgOrient;
    File imgFile;
    String currentPath;
    String oppId;
    String myId;
    ArrayList<Chat> chatList;

    String preDate;
    String curDate;
    Thread recvThread;
    boolean isRunThread;
    boolean isRemove;

    ActivityChatroomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatroom);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatList.setLayoutManager(manager);
        mProfile = SaveSharedPreference.getMyProfile();
        chatRoom = getIntent().getParcelableExtra("SELECTED_CHATROOM");
        chatsFile = new File(DIRECTORY_PATH + "/chat/" + chatRoom.getProfile().getId() + ".txt");
        chatList = chatRoom.getChatList();
        loadChatsFromFile();

        myId = mProfile.getId();
        oppId = chatRoom.getProfile().getId();
        Log.d("경로", DIRECTORY_PATH);

        setShowDate();
        Log.d(TAG, chatList.size() + "");
        if (chatList.size() == 1) {
            connReqIdList = SaveSharedPreference.getConnectionRequestedIdList();
            for (String id : connReqIdList) {
                Log.d("connReqIdList", id);
                if (oppId.equals(id)) {
                    chatList.add(new Chat(CONNECT_REQUEST_BY_ME, null, getMsgSendTime(), OPPONENT_TURN));
                    chatRoom.setChatList(chatList);
                    isExistReqId = true;
                    break;
                }
            }
            if (!isExistReqId) {
                chatList.add(new Chat(CONNECT_REQUEST_BY_OPPONENT, null, getMsgSendTime(), OPPONENT_TURN));
                chatRoom.setChatList(chatList);
            }
            binding.etInputMsg.setEnabled(false);
            binding.ivSendMsg.setEnabled(false);
            binding.ivSendMsg.setAlpha(DISABLED_ALPHA);
            binding.ivSendImage.setEnabled(false);
            binding.ivSendImage.setAlpha(DISABLED_ALPHA);
        }
        isRunThread = true;
        recvThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunThread) {
                    StringBuffer sendMsgSb = new StringBuffer();
                    sendMsgSb.append(RECEIVE_MSG).append("$");
                    sendMsgSb.append(myId).append("$");
                    sendMsgSb.append(oppId);
                    RecvMsgTask mRecvMsgTask = new RecvMsgTask(sendMsgSb.toString());
                    mRecvMsgTask.execute();
                    if (chatList.size() > 2) {
                        if (!isRemove) {
                            SaveSharedPreference.removeConnectionRequestId(oppId);
                            isRemove = true;
                        }
                        binding.etInputMsg.setEnabled(true);
                        binding.ivSendMsg.setEnabled(true);
                        binding.ivSendMsg.setAlpha(ENABLED_ALPHA);
                        binding.ivSendImage.setEnabled(true);
                        binding.ivSendImage.setAlpha(ENABLED_ALPHA);
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        recvThread.start();

        String oppName = chatRoom.getProfile().getName();
        binding.tvChatRoomOppName.setText(oppName);

        chatAdapter = new ChatAdapter(chatRoom, this);
        binding.rvChatList.setAdapter(chatAdapter);

        binding.ivChatRoomBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.ivSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.etInputMsg.getText().toString();
                String time = getMsgSendTime();
                chatRoom.getChatList().add(new Chat(msg, null, time, MY_TURN));