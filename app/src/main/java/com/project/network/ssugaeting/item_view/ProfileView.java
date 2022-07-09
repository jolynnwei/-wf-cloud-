
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
