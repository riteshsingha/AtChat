package com.example.atchat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatBox extends AppCompatActivity {
    private String groupRef;
    private static final String TAG = "ChatBox";
    private Button btnMsgSend;
    private EditText editTextMsg;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        btnMsgSend = findViewById(R.id.msgSendBtn);
        editTextMsg = findViewById(R.id.msgEditText);
        recyclerView = findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String type = getIntent().getStringExtra("type");

        if (type.equals("one2one")) {
            one2oneChatExecution();
        } else if (type.equals("groupChat")) {

            groupChatExecution();
        }


    }

    private void one2oneChatExecution() {

        final String userId = getIntent().getStringExtra("link");
        final String userName = getIntent().getStringExtra("name");
        final String imgLink = getIntent().getStringExtra("photo");
        Log.d(TAG, "onCreate: Users ID " + userId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(userName);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //For Recycler View and writing message
        final DatabaseReference myChatLink = FirebaseDatabase.getInstance().getReference()
                .child("ChatData").child("UserChat").child(currentUser.getUid())
                .child(userId);

        // Only for writing message
        final DatabaseReference userChatLink = FirebaseDatabase.getInstance().getReference()
                .child("ChatData").child("UserChat").child(userId).child(currentUser.getUid());

        MsgAdapter adapter = new MsgAdapter(getApplicationContext()
                , myChatLink.child("convo").toString(), recyclerView);
        recyclerView.setAdapter(adapter);

        btnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgData = editTextMsg.getText().toString().trim();
                if (!msgData.equals("")) {
                    sendMessage121(msgData, myChatLink, userChatLink, userId
                            , imgLink, userName);
                } else editTextMsg.setError("Cannot be empty");

            }


        });
    }

    private void sendMessage121(String msgData,
                                DatabaseReference myChatLink,
                                DatabaseReference userChatLink, String userId
            , String imgLink, String userName) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().push();
        String key = ref.getKey();
        Map map = new HashMap();
        map.put("textMsg", msgData);
        map.put("key", key);
        map.put("senderId", currentUser.getUid());
        map.put("senderName", currentUser.getDisplayName());
        map.put("timeStamp", ServerValue.TIMESTAMP);
        Log.d(TAG, "sendMessage121: MyChatLink" + myChatLink.toString());
        Log.d(TAG, "sendMessage121: UserChatLink" + userChatLink.toString());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Map map1 = new HashMap();
        map1.put("userId", userId);
        map1.put("userName", userName);
        map1.put("userImg", imgLink);
        map1.put("lastMsg",msgData);
        map1.put("msgStamp",ServerValue.TIMESTAMP);

        Map map2 = new HashMap();
        map2.put("userId", MainActivity.userId);
        map2.put("userName", currentUser.getDisplayName());
        map2.put("userImg", currentUser.getPhotoUrl().toString());
        map2.put("lastMsg",msgData);
        map2.put("msgStamp",ServerValue.TIMESTAMP);

        //For Updating User Detail in User Chat Node
        myChatLink.updateChildren(map1);
        userChatLink.updateChildren(map2);

        //For Message Uploading
        myChatLink.child("convo").child(key).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editTextMsg.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
        userChatLink.child("convo").child(key).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // editTextMsg.setText("");
            }
        });


    }


    private void groupChatExecution() {
        groupRef = getIntent().getStringExtra("link");
        Log.d(TAG, "onCreate: GroupLink " + groupRef);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        MsgAdapter adapter = new MsgAdapter(getApplicationContext(), groupRef, recyclerView);
        recyclerView.setAdapter(adapter);

        msgExecution();

    }

    private void msgExecution() {
        btnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgData = editTextMsg.getText().toString().trim();
                if (!msgData.equals("")) {
                    sendMessage(msgData);
                } else editTextMsg.setError("Cannot be empty");

            }


        });
    }

    private void sendMessage(String msgData) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().push();
        String key = ref.getKey();
        DatabaseReference reference = FirebaseDatabase.
                getInstance().getReferenceFromUrl(groupRef).child(key);
        Map map = new HashMap();
        map.put("textMsg", msgData);
        map.put("key", key);
        map.put("senderId", currentUser.getUid());
        map.put("senderName", currentUser.getDisplayName());
        map.put("timeStamp", ServerValue.TIMESTAMP);
        reference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editTextMsg.setText("");
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
