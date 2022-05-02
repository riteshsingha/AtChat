package com.example.atchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

public class UserProfile extends AppCompatActivity {

    private static final int IMG_REQUEST = 100;
    private DatabaseReference userRef;
    private String userName,userImg,userId,userEmail;
    private CircleImageView imgUser;
    private TextView tvEmail,tvName;
    private Button btnChat;
    private ImageView galleryClick;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "UserProfile";
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        imgUser=findViewById(R.id.userImg);
        tvEmail=findViewById(R.id.userEmail);
        tvName=findViewById(R.id.userName);
        btnChat=findViewById(R.id.btnChat);
        galleryClick=findViewById(R.id.galleryClick);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        setAndGetUser();

        galleryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMG_REQUEST);*/

                // Crop Image library used
                //  api 'com.theartofdev.edmodo:android-image-cropper:2.8.+'
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(UserProfile.this);
               // CropImage.activity(imageUri)
                     //   .start(UserProfile.this);

            }
        });


        if (currentUser.getUid().equals(userId)) {
            btnChat.setVisibility(View.GONE);
            galleryClick.setVisibility(View.VISIBLE);
        }else {
            btnChat.setVisibility(View.VISIBLE);
            galleryClick.setVisibility(View.GONE);
        }
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity
                        (new Intent(getApplicationContext(),ChatBox.class)
                                .putExtra("link",userId)
                                .putExtra("name",userName)
                                .putExtra("photo",userImg)
                                .putExtra("type","one2one"));

            }
        });

    }

    // Crop Image library Used To Crop Image
    //   api 'com.theartofdev.edmodo:android-image-cropper:2.8.+'
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode ==IMG_REQUEST && resultCode==RESULT_OK){
            Uri uri=data.getData();

            updateProfilePic(uri);
            Log.d(TAG, uri.toString() );

        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d(TAG, "onActivityResult: "+resultUri.toString());
                updateProfilePic(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateProfilePic(Uri uri) {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference()
                .child("ProfilePics").child(userId);

        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getMetadata().getReference().getDownloadUrl().
                            addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String photoUrl=task.getResult().toString();

                                        Map map=new HashMap();
                                        map.put("userImg",photoUrl);
                                        userRef.updateChildren(map);
                                        Toast.makeText(UserProfile.this, "Image Updated",
                                                Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                }
            }
        });
    }

    private void setAndGetUser() {
        userId=getIntent().getStringExtra("userId");
        userRef= FirebaseDatabase.getInstance().getReference()
                .child("UserData").child("Users").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Helper helper = dataSnapshot.getValue(Helper.class);
                    userEmail = helper.getUserEmail();
                    userImg = helper.getUserImg();
                    userName = helper.getUserName();
                    getSupportActionBar().setTitle(userName);
                    tvName.setText(userName);
                    tvEmail.setText(userEmail);
                    btnChat.setText("Chat With "+userName);



                    Glide
                            .with(getApplicationContext())
                            .load(helper.getUserImg())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            //.placeholder(R.drawable.gif_spinner_move_forward)
                            .into(imgUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
