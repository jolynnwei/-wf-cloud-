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
   