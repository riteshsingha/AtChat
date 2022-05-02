package com.example.atchat;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

class UserHolder extends RecyclerView.ViewHolder {
    private CircleImageView userImg;
    private TextView textViewName,tvLastMessage;
    private View rootView;
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        userImg=itemView.findViewById(R.id.userImg);
        textViewName=itemView.findViewById(R.id.userName);
        rootView=itemView.findViewById(R.id.view);
        tvLastMessage=itemView.findViewById(R.id.tvLastMsg);
    }

    public void configHolder(UserHolder userHolder, int i, final Helper helper) {

        textViewName.setText(helper.getUserName());
        Glide
                .with(itemView.getContext())
                .load(helper.getUserImg())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.gif_spinner_move_forward)
                .into(userImg);

     /*   if(MainActivity.userId.equals(helper.getUserId())){
            rootView.setVisibility(View.GONE);
        }else {
            rootView.setVisibility(View.VISIBLE);
        }*/
        if(helper.getLastMsg()!=null){
            tvLastMessage.setVisibility(View.VISIBLE);
            tvLastMessage.setText(helper.getLastMsg());



            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemView.getContext().startActivity
                            (new Intent(itemView.getContext(),ChatBox.class)
                                    .putExtra("link",helper.getUserId())
                                    .putExtra("name",helper.getUserName())
                                    .putExtra("photo",helper.getUserImg())
                                    .putExtra("type","one2one"));

                }
            });


        }else {
            tvLastMessage.setVisibility(View.GONE);


            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext()
                            ,UserProfile.class).putExtra("userId",helper.getUserId()));

                }
            });
        }


    }
}
