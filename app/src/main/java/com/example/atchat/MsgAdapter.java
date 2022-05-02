package com.example.atchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

class MsgAdapter extends RecyclerView.Adapter {
    List<Helper> msgList=new ArrayList<>();
    public MsgAdapter(Context applicationContext, String groupRef,
                      final RecyclerView recyclerView) {
        DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReferenceFromUrl(groupRef);

       reference.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        Helper helper=dataSnapshot.getValue(Helper.class);
                        msgList.add(helper);
                        notifyItemInserted(msgList.size()-1);
                        recyclerView.scrollToPosition(msgList.size()-1);
                    }

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       }) ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 1: View view1=inflater.inflate(R.layout.list_my_msg,parent,false);
                viewHolder=new MyMsgHolder(view1);
                break;
            case 2: View view2=inflater.inflate(R.layout.list_other_msg,parent,false);
                viewHolder=new OtherMsgHolder(view2);
                break;
            default: View view3=inflater.inflate(R.layout.list_my_msg,parent,false);
                viewHolder=new MyMsgHolder(view3);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case 1: MyMsgHolder myMsgHolder =(MyMsgHolder)viewHolder;
                myMsgHolder.configHolder(myMsgHolder,msgList,position);
                break;
            case 2:
                OtherMsgHolder otherMsgHolder =(OtherMsgHolder)viewHolder;
                otherMsgHolder.configHolder(otherMsgHolder,msgList,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(msgList.get(position).getSenderId().equals(MainActivity.userId)){
           return 1;
       }else {
           return 2;
       }
    }
}
