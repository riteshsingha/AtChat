package com.example.atchat;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

class OtherMsgHolder extends RecyclerView.ViewHolder {
    private TextView senderName,textMsg;
    public OtherMsgHolder(View itemView) {
        super(itemView);
        senderName=itemView.findViewById(R.id.tvType);
        textMsg=itemView.findViewById(R.id.tvDesc);
    }

    public void configHolder(OtherMsgHolder otherMsgHolder,
                             List<Helper> msgList, int position) {

        senderName.setText(msgList.get(position).getSenderName());
        textMsg.setText(msgList.get(position).getTextMsg());

    }
}
