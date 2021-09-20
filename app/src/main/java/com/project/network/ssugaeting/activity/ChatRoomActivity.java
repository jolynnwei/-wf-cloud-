
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
                chatAdapter.notifyDataSetChanged();
                binding.rvChatList.scrollToPosition(chatRoom.getChatList().size() - 1);
                binding.etInputMsg.setText(null);

                StringBuffer sendMsgSb = new StringBuffer();
                sendMsgSb.append(SEND_MSG).append("$");
                sendMsgSb.append(myId).append("$");
                sendMsgSb.append(oppId).append("$");
                sendMsgSb.append(msg).append("$");
                sendMsgSb.append(" ").append("$");
                sendMsgSb.append(time);
                SendMsgTask mSendMsgTask = new SendMsgTask(sendMsgSb.toString());
                mSendMsgTask.execute();
            }
        });

        binding.ivSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "전송할 사진을 선택하세요."), IMAGE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final Uri filePathURI = data.getData();
            final String imageURI = filePathURI.toString();
            final String time = getMsgSendTime();
            chatAdapter.notifyDataSetChanged();
            binding.rvChatList.scrollToPosition(chatRoom.getChatList().size() - 1);
            getImageNameToUri(filePathURI);
            imgFile = new File(mImgPath);
            // upload to ftp
            FtpUploadTask mFtpUploadTask = new FtpUploadTask(imageURI, time);
            mFtpUploadTask.execute();

        }
    }

    @Override
    public void onBackPressed() {
        isRunThread = false;
        if (chatRoom.getChatList().size() > 2) {
            chatRoom.getChatList().remove(1);
            saveChatsToFile();
        }
        Intent intent = new Intent(ChatRoomActivity.this, MainActivity.class);
        intent.putExtra("position", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String getMsgSendTime() {
        String currentTime;
        Date date = new Date();
        if (date.getHours() < 12)
            currentTime = "오전 " + date.getHours() + ":" + String.format("%02d", date.getMinutes());
        else {
            if (date.getHours() == 12)
                currentTime = "오후 " + date.getHours() + ":" + String.format("%02d", date.getMinutes());
            else
                currentTime = "오후 " + (date.getHours() - 12) + ":" + String.format("%02d", date.getMinutes());
        }
        return currentTime;
    }

    private void setShowDate() {
        if (chatList.isEmpty())
            chatList.add(new Chat(null, null, getDate(), DATE_TURN));
        else {

            for (Chat c : chatList)
                if (c.getMsgTime().equals(getDate()))
                    return;
            chatList.add(new Chat(null, null, getDate(), DATE_TURN));
        }
    }

    private String getDate() {
        String retDate;
        Date date = new Date();
        SimpleDateFormat formatType = new SimpleDateFormat("yyyy년 MM월 dd일 ");
        retDate = formatType.format(date);
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        retDate += DAY_OF_WEEK[dayOfWeek];
        return retDate;
    }


    private void saveChatsToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(chatsFile, false);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(fos));
            Log.d("chatList.size", chatList.size() + "");
            for (Chat chat : chatList) {
                os.writeObject(chat);
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatsFromFile() {
        if (!chatsFile.exists()) {
            Log.d(TAG, "File of chats is not exist");
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(chatsFile);
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fis));

            chatList.clear();
            while (true) {
                Chat mChat = (Chat) in.readObject();
                if (mChat == null)
                    break;
                chatList.add(mChat);
            }
            chatRoom.setChatList(chatList);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // URI 정보를 이용하여 사진 정보 가져온다
    private void getImageNameToUri(Uri data) {
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();
        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);

        mImgPath = cursor.getString(column_data);
        mImgTitle = cursor.getString(column_title);
        mImgOrient = cursor.getString(column_orientation);
    }

    private class FtpUploadTask extends AsyncTask<Void, Void, String> {
        private String imageURI;
        private String time;

        public FtpUploadTask(String imageURI, String time) {
            this.imageURI = imageURI;
            this.time = time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            chatRoom.getChatList().add(new Chat(null, currentPath, time, MY_TURN));
            chatAdapter.notifyDataSetChanged();
            binding.rvChatList.scrollToPosition(chatRoom.getChatList().size() - 1);
            ftpConnection.ftpDisconnect();

            StringBuffer sendImgSb = new StringBuffer();
            sendImgSb.append(SEND_IMAGE).append("$");
            sendImgSb.append(myId).append("$");
            sendImgSb.append(oppId).append("$");
            sendImgSb.append(" ").append("$");
            sendImgSb.append(currentPath).append("$");
            sendImgSb.append(time);
            SendMsgTask mSendMsgTask = new SendMsgTask(sendImgSb.toString());
            mSendMsgTask.execute();
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            ftpStatus = ftpConnection.ftpConnect();
            if (ftpStatus)
                Log.d(TAG, "FTP 연결 성공");
            else
                Log.d(TAG, "FTP 연결 실패");
            ftpConnection.ftpChangeDirectory("msg/");
            ftpConnection.ftpUploadFile(imgFile, imageURI);
            currentPath = ftpConnection.ftpGetDirectory();
            currentPath += "/" + mImgTitle + ".jpg";
            Log.d("FTP 이미지 업로드", currentPath);
            return "";
        }
    }

    private class SendMsgTask extends AsyncTask<Void, Void, String> {
        private String values;

        public SendMsgTask(String values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
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

    private class RecvMsgTask extends AsyncTask<Void, Void, String> {
        private String values;

        public RecvMsgTask(String values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("FAIL")) {
                return;
            } else if (result == null) {
                return;
            } else {
                StringTokenizer msgTokenizer = new StringTokenizer(result, "$");
                final String msg = msgTokenizer.nextToken();
                final String ftpPath = msgTokenizer.nextToken();
                final String time = msgTokenizer.nextToken();
                if (ftpPath.equals(" "))
                    chatRoom.getChatList().add(new Chat(msg, null, time, OPPONENT_TURN));
                else if (msg.equals(" ")) {
                    final Thread ftpThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String imgURI;
                            ftpStatus = ftpConnection.ftpConnect();
                            if (ftpStatus)
                                Log.d(TAG, "FTP 연결 성공");
                            else
                                Log.d(TAG, "FTP 연결 실패");
                            ftpConnection.ftpChangeDirectory("msg/");
                            int idx = ftpPath.indexOf("msg/");
                            String title = ftpPath.substring(idx, ftpPath.length());
                            imgURI = DIRECTORY_PATH + "/chat";
                            imgURI += "/" + title;
                            ftpConnection.ftpDownloadFile(ftpPath, imgURI);
                            chatRoom.getChatList().add(new Chat(null, imgURI, time, OPPONENT_TURN));
                            ftpConnection.ftpDisconnect();
                        }
                    });
                    ftpThread.start();
                }
                chatAdapter.notifyDataSetChanged();
                binding.rvChatList.scrollToPosition(chatRoom.getChatList().size() - 1);
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