
package com.project.network.ssugaeting.item_view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.network.ssugaeting.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jin on 2018-04-08.
 */

public class ProfileView extends LinearLayout {
    TextView userNameText;
    TextView userStateMsgText;
    CircleImageView userPhotoImage;

    public ProfileView(Context context) {
        super(context);
        init(context);
    }

    public ProfileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_profile, this, true);

        userNameText = (TextView) findViewById(R.id.tv_userName);
        userStateMsgText = (TextView) findViewById(R.id.tv_userStateMsg);
        userPhotoImage = (CircleImageView) findViewById(R.id.iv_userProImage);
    }

    public void setName(String name) {
        userNameText.setText(name);
    }

    public void setStateMsg(String stateMsg) {
        userStateMsgText.setText(stateMsg);
    }

    public void setImageURL(String imageURI) {
        userPhotoImage.setImageURI(Uri.parse(imageURI));
    }
}