
package com.project.network.ssugaeting.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.activity.ChatRoomActivity;
import com.project.network.ssugaeting.activity.FilterActivity;
import com.project.network.ssugaeting.adapters.ChatRoomAdapter;
import com.project.network.ssugaeting.databinding.FragmentChatroomListBinding;
import com.project.network.ssugaeting.http_connect.RequestHttpURLConnection;
import com.project.network.ssugaeting.item.Chat;
import com.project.network.ssugaeting.item.ChatRoom;
import com.project.network.ssugaeting.item.Profile;
import com.project.network.ssugaeting.shared_preference.SaveSharedPreference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.project.network.ssugaeting.activity.ChatRoomActivity.OPPONENT_TURN;
import static com.project.network.ssugaeting.activity.MainActivity.DIRECTORY_PATH;

public class ChatRoomListFragment extends Fragment {
    private static final String TAG = "ChatRoomListFragment";
    private static final int LOAD_PROFILE_LIST = 7;
    private static final char LOAD_CHATROOM_LIST = 'd';

    private ChatRoomAdapter chatRoomAdapter;
    FragmentChatroomListBinding binding;
    File chatsFile;
    HashMap<String, Integer> id_msgCnt = new HashMap<String, Integer>(100);
    int msgCnt;
    ArrayList<ChatRoom> chatRoomList;
    ArrayList<Profile> profileList;
    ArrayList<Chat> chatList;
    private ArrayList<String> connReqIdList;
    Profile mProfile;
    Thread recvThread;
    boolean isRunThread;
    ProgressDialog progressDialog;

    public ChatRoomListFragment() {
    }

    public static ChatRoomListFragment newInstance() {
        return new ChatRoomListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatroom_list, container, false);
        mProfile = SaveSharedPreference.getMyProfile();

        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoomList = new ArrayList<>(100);
                profileList = new ArrayList<>(100);
                chatList = new ArrayList<>(100);
                StringBuffer loadProfileListSb = new StringBuffer();
                loadProfileListSb.append(LOAD_PROFILE_LIST).append("$");
                loadProfileListSb.append(mProfile.getId());
                LoadProfileListTask mLoadProfileListTask = new LoadProfileListTask(loadProfileListSb.toString());
                mLoadProfileListTask.execute();

                StringBuffer loadChatRoomListSb = new StringBuffer();
                loadChatRoomListSb.append(LOAD_CHATROOM_LIST).append("$");
                loadChatRoomListSb.append(mProfile.getId());
                LoadChatRoomListTask mLoadChatRoomListTask = new LoadChatRoomListTask(loadChatRoomListSb.toString());
                mLoadChatRoomListTask.execute();
            }
        });

       /* id_msgCnt.put("id", 12);
        Profile oProfile = new Profile("id", "pw", "email@soongsil,com", "user", "남성", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ");
        chatsFile = new File(DIRECTORY_PATH + "/chat/" + oProfile.getId() + ".txt");
        loadChatsFromFile();
        // if (chats != null) Log.d(TAG, "chats is not null");
        chatRoomList.add(new ChatRoom(chatList, oProfile, id_msgCnt.get("id")));
        chatRoomAdapter = new ChatRoomAdapter(getContext());
        for (ChatRoom chatRoom : chatRoomList)
            chatRoomAdapter.addItem(chatRoom);
        binding.lvChatRoomList.setAdapter(chatRoomAdapter);
*/

        binding.lvChatRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoom item = (ChatRoom) chatRoomAdapter.getItem(position);
                Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("SELECTED_CHATROOM", item);
                startActivity(intent);
            }
        });

        View view = binding.getRoot();
        return view;
    }

    private class LoadProfileListTask extends AsyncTask<Void, Void, String> {
        private String values;

        public LoadProfileListTask(String values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("FAIL"))
                return;
            if (result != null) {
                StringTokenizer resultTokenizer = new StringTokenizer(result, "|");
                while (resultTokenizer.hasMoreTokens()) {
                    String profileContent = resultTokenizer.nextToken();
                    Profile oProfile = getProfileByMessage(profileContent);
                    profileList.add(oProfile);
                }
            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
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

    private Profile getProfileByMessage(String message) {
        Log.d("message", message);
        StringTokenizer tokenizer = new StringTokenizer(message, "$");
        String id = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        String email = tokenizer.nextToken();
        String name = tokenizer.nextToken();
        String sex = tokenizer.nextToken();