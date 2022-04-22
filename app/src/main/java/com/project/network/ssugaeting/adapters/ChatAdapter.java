package com.project.network.ssugaeting.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.network.ssugaeting.activity.ImageActivity;
import com.project.network.ssugaeting.databinding.ItemChatBinding;
import com.project.network.ssugaeting.ftp.FTPConnection;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.ChatRoom;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.network.ssugaeting.activity.ChatRoomActivity.CONNECT_REQUEST_BY_OPPONENT;
import static com.project.network.ssugaeting.activity.ChatRoomActivity.DATE_TURN;
import static com.project.network.ssugaeting.activity.ChatRoomActivity.MY_TURN;
import static com.project.network.ssugaeting.activity.ChatRoomActivity.OPPONENT_TURN;
import static com.project.network.ssugaeting.activity.MainActivity.DIRECTORY_PATH;
import static com.project.network.ssugaeting.activity.MainActivity.FTP_PATH;

/**
 * Created by Jin on 2018-05-16.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ChatAdapter";
    private static final int CONNECT_RESPONSE = 9;

    private ChatRoom chatRoom;
    private Context context;
    private int oppMsgCnt;
    ProgressDialog progressDialog;

    private FTPConnection ftpConnection = new FTPConnection();
    ArrayList<Chat> chatList;
    Profile oProfile;
    boolean ftpStatus;
    String currentPath;

    String mImgPath;
    String mImgTitle;
    String mImgOrient;

    public ChatAdapter(ChatRoom chatRoom, Context context) {
        this.chatRoom = chatRoom;
        this.context = context;
        oppMsgCnt = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false);
        holder = new ChatHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatHolder itemViewHolder = (ChatHolder) holder;
        final ItemChatBinding binding = itemViewHolder.binding;
        chatList = chatRoom.getChatList();
        oProfile = chatRoom.getProfile();

        final int msgTurn = chatList.get(position).getMsgTurn();
        String sendMsg = chatList.get(position).getSendMsg();
        final String sendImgURI = chatList.get(position).getSendImageURI();
        final String curMsgTime = chatList.get(position).getMsgTime();
        String pstMsgTime = "";
        if (position > 1) pstMsgTime = chatList.get(position - 1).getMsgTime();
        final String userName = oProfile.getName();
        final String userProImageURI = oProfile.getImageURI();
        binding.tvOppChatName.setText(userName);
        if (!userProImageURI.equals(" ")) {
            Log.d("userProImageURI", userProImageURI);
            FtpDownloadProTask mFtpDownloadProTask = new FtpDownloadProTask(userProImageURI, binding);
            mFtpDownloadProTask.execute();
        }

        switch (msgTurn) {
            case MY_TURN:
                oppMsgCnt = 0;
                binding.rlOppChat.setVisibility(View.GONE);
                binding.rlMyChat.setVisibility(View.VISIBLE);
                binding.llShowDate.setVisibility(View.GONE);
                // 메시지 전송인 경우
                if (sendImgURI == null) {
                    binding.tvMyChatMsg.setVisibility(View.VISIBLE);
                    binding.tvMyChatTime.setVisibility(View.VISIBLE);
                    binding.ivMySendImage.setVisibility(View.GONE);
                    binding.tvMyImageTime.setVisibility(View.GONE);
                    binding.tvMyChatMsg.setText(sendMsg);
                    binding.tvMyChatTime.setText(curMsgTime);
                }
                // 이미지 전송인 경우
                else {
                    binding.tvMyChatMsg.setVisibility(View.GONE);
                    binding.tvMyChatTime.setVisibility(View.GONE);
                    binding.ivMySendImage.setVisibility(View.VISIBLE);
                    binding.tvMyImageTime.setVisibility(View.VISIBLE);
                    binding.tvMyImageTime.setText(curMsgTime);
                    binding.ivMySendImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context.getApplicationContext(), ImageActivity.class);
                            intent.putExtra("SELECTED_IMAGE", sendImgURI);
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case OPPONENT_TURN:
                if (sendMsg.equals(CONNECT_REQUEST_BY_OPPONENT)) {
                    showConnectRequest(binding);
                    return;
                } else {
                    binding.llConnectRequest.setVisibility(View.GONE);
                }
                if (sendMsg.equals("CONNECTED")) {
                    sendMsg = "상대방과 연결되었습니다.";
                }
                oppMsgCnt++;
                binding.rlMyChat.setVisibility(View.GONE);
                binding.rlOppChat.setVisibility(View.VISIBLE);
                binding.llShowDate.setVisibility(View.GONE);
                binding.tvOppChatMsg.setText(sendMsg);
                binding.tvOppChatTime.setText(curMsgTime);
                if (oppMsgCnt > 1 && pstMsgTime.equals(curMsgTime)) {
                    binding.tvOppChatName.setVisibility(View.GONE);
                    binding.ivOppProImage.setVisibility(View.INVISIBLE);
                }
                // 메시지 전송인 경우
                if (sendImgURI == null) {
                    binding.tvOppChatMsg.setVisibility(View.VISIBLE);
                    binding.tvOppChatTime.setVisibility(View.VISIBLE);
                    binding.ivOppRecvImage.setVisibility(View.GONE);
                    binding.tvOppImageTime.setVisibility(View.GONE);
                    binding.tvOppChatMsg.setText(sendMsg);
                    binding.tvOppChatTime.setText(curMsgTime);
                }
                // 이미지 전송인 경우
                else {
                    binding.tvOppChatMsg.setVisibility(View.GONE);
                    binding.tvOppChatTime.setVisibility(View.GONE);
                    binding.ivOppRecvImage.setVisibility(View.VISIBLE);
                    binding.tvOppImageTime.setVisibility(View.VISIBLE);
                    binding.tvOppImageTime.setText(curMsgTime);
                    binding.ivOppRecvImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context.getApplicationContext(), ImageActivity.class);
                            intent.putExtra("SELECTED_IMAGE", sendImgURI);
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case DATE_TURN:
                binding.rlOppChat.setVisibility(View.GONE);
                binding.rlMyChat.setVisibility(View.GONE);
                binding.llShowDate.setVisibility(View.VISIBLE);
                binding.tvDate.setText(curMsgTime);
        }
    }

    @Override
    public int getItemCount() {
        return chatRoom.getChatList().size();
    }

    private class ChatHolder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;

        ChatHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void showConnectRequest(ItemChatBinding binding) {
        binding.rlOppChat.setVisibility(View.VISIBLE);
        binding.rlMyChat.setVisibility(View.GONE);
        binding.llConnectRequest.setVisibility(View.VISIBLE);

        binding.btnReqAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile mProfile = SaveSharedPreference.getMyProfile();
                StringBuffer connResSb = new StringBuffer();
                connResSb.append(CONNECT_RESPONSE).append("$");
                connResSb.append(mProfile.getId()).append("$");
                connResSb.append(oProfile.getId()).append("$");
                connResSb.append("1");
                ConnectResponseTask mConnectResponseTask = new ConnectResponseTask(connResSb.toString());
                mConnectResponseTask.execute();
            }
        });

        binding.btnReqReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile mProfile = SaveSharedPreference.getMyProfile();
                StringBuffer connResSb = new StringBuffer();
                connResSb.append(CONNECT_RESPONSE).append("$");
                connResSb.append(mProfile.getId()).append("$");
                connResSb.append(oProfile.getId()).append("$");
                connResSb.append("0");
                ConnectResponseTask mConnectResponseTask = new ConnectResponseTask(connResSb.toString());
                mConnectResponseTask.execute();
            }
        });
    }

    private class FtpDownloadProTask extends AsyncTask<Void, Void, String> {
        private String imageURI;
        private ItemChatBinding binding;

        public FtpDownloadProTask(String imageURI, ItemChatBinding binding) {
            this.imageURI = imageURI;
            this.binding = binding;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            binding.ivOppProImage.setImageURI(Uri.parse(result));
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String imgURI;
            ftpStatus = ftpConnection.ftpConnect();
            if (ftpStatus)
                Log.d(TAG, "FTP 연결 성공");
            else
                Log.d(TAG, "FTP 연결 실패");
            ftpConnection.ftpChangeDirectory("profile/");
            imgURI = DIRECTORY_PATH + "/profile";
            imgURI += "/" + oProfile.getId() + ".jpg";
            ftpConnection.ftpDownloadFile(FTP_PATH + "/profile/" + oProfile.getId() + ".jpg", imgURI);
            ftpConnection.ftpDisconnect();
            return imgURI;
        }
    }

    private class FtpDownloadMsgTask extends AsyncTask<Void, Void, String> {
        private String imageURI;
        private ImageView imageView;

        public FtpDownloadMsgTask(String imageURI, ImageView imageView) {
            this.imageURI = imageURI;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            imageView.setImageURI(Uri.parse(result));
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String imgURI;
            ftpStatus = ftpConnection.ftpConnect();
            if (ftpStatus)
                Log.d(TAG, "FTP 연결 성공");
            else
                Log.d(TAG, "FTP 연결 실패");
            imgURI = DIRECTORY_PATH + "/chat";
            int idx = imageURI.indexOf("msg/");
            String title = imageURI.substring(idx+4, imageURI.length());
            Log.d("title", title);
            imgURI += "/" + title;
            ftpConnection.ftpDownloadFile(imageURI, imgURI);
            Log.d("다운로드된 파일", imgURI);
            ftpConnection.ftpDisconnect();
            return imgURI;
        }
    }


    private class ConnectResponseTask extends AsyncTask<Void, Void, String> {
        private String values;

        public ConnectResponseTask(String values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Connecting Server", "Please wait...", false, false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equals("UNCONNECTED")) {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_LONG).show();
            } else if (result.equals("ACCEPT")) {
                Toast.makeText(context, "수락하였습니다.", Toast.LENGTH_LONG).show();
            } else if (result.equals("REJECT")) {
                Toast.makeText(context, "거절하였습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "전달된 데이터가 없습니다.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(values);
            Log.d(TAG, result);
            int idx = result.indexOf("&");
            result = result.substring(0, idx);
            return result;
        }
    }

    private void getImageNameToUri(Uri data) {
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = context.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();
        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_orientation = cursor.getColumnInd