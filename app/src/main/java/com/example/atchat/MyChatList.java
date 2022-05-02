package com.example.atchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyChatList extends Fragment {
    private static final String LOGIN_CHECK = "LOGIN";
    private DatabaseReference groupChatRef;
    private FirebaseAuth mAuth;
    private TextView groupChatTextView;
    public static String userId;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Helper, UserHolder> adapter;
    private FirebaseUser currentUser;
    public MyChatList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            groupChatExecution();
            one2onceChatExecuton();

        }
    }
    private void one2onceChatExecuton() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference()
                .child("ChatData").child("UserChat").child(currentUser.getUid());
      /* DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference()
               .child("UserData").child("Users");*/
        Query query=databaseReference.orderByChild("msgStamp");
        FirebaseRecyclerOptions<Helper> options =
                new FirebaseRecyclerOptions.Builder<Helper>()
                        .setQuery(query, Helper.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<Helper, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder userHolder, int i,
                                            @NonNull Helper helper) {
                userHolder.configHolder(userHolder,i,helper);
            }

            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list,parent,false);
                return new UserHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void groupChatExecution() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        groupChatRef = database.getReference("ChatData")
                .child("GroupChat").child("Conversation");
        groupChatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ChatBox.class)
                        .putExtra("link", groupChatRef.toString())
                        .putExtra("name","Group Chat")
                        .putExtra("type","groupChat"));

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_chat_list, container, false);



        groupChatTextView=view.findViewById(R.id.groupChatTextView);
        recyclerView=view.findViewById(R.id.recyclerView);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setStackFromEnd(true);mManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mManager);
        return view;
    }

}



