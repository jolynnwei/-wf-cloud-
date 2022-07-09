
package com.project.network.ssugaeting.item_view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.network.ssugaeting.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jin on 2018-04-08.
 */

public class ChatRoomView extends LinearLayout {
    TextView userNameText;
    TextView userMsgText;
    TextView msgTimeText;
    TextView msgCountText;
    CircleImageView userPhotoImage;

    public ChatRoomView(Context context) {
        super(context);
        init(context);
    }

    public ChatRoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_chatroom, this, true);

        userNameText = (TextView) findViewById(R.id.tv_chatName);
        userMsgText = (TextView) findViewById(R.id.tv_chatMsg);
        msgTimeText = (TextView) findViewById(R.id.tv_chatTime);
        msgCountText = (TextView) findViewById(R.id.tv_chatCnt);
        userPhotoImage = (CircleImageView) findViewById(R.id.iv_chatProImage);
    }

    public void setUserName(String userName) {
        userNameText.setText(userName);
    }

    public void setUserMsg(String userMsg) {
        userMsgText.setText(userMsg);
    }

    public void setMsgTime(String msgTime) {
        msgTimeText.setText(msgTime);
    }

    public void setMsgCount(int msgCount) {
        msgCountText.setText("" + msgCount);
        if (msgCount == 0)
            msgCountText.setVisibility(INVISIBLE);
        else
            msgCountText.setVisibility(VISIBLE);
    }

    public void setUserImageURL(String imageURI) {
        userPhotoImage.setImageURI(Uri.parse(imageURI));
    }

}