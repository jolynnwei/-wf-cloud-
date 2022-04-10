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
