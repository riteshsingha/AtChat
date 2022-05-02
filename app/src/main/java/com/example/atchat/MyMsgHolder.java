package com.example.atchat;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

class MyMsgHolder extends RecyclerView.ViewHolder {
    private TextView senderName,textMsg;
    public MyMsgHolder(View itemView) {
        super(itemView);
        senderName=itemView.findViewById(R.id.tvType);
        textMsg=itemView.findViewById(R.id.tvDesc);
    }

    public void configHolder(MyMsgHolder myMsgHolder, List<Helper> msgList, int position) {
        senderName.setText(msgList.get(position).getSenderName());
        textMsg.setText(msgList.get(position).getTextMsg());

    }
}
