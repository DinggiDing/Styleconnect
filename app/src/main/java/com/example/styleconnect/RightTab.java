package com.example.styleconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RightTab extends AppCompatActivity {
    private Context context;
    private View view;
    private CircleImageView profile_img;
    private ImageButton plus_icon;
    private AppCompatButton delete_move_Btn;
    private TextView show_username, show_id, show_level, level;
    private ImageView trophy;
    private RelativeLayout membership_rectangle;
    private String ID;
    private Uri imageUri;
    private FirebaseFirestore db;
   // private StorageReference mStorageReference;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
    StorageReference mStorageReference = storage.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_righttab);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.about);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    case R.id.dashboard: {
                        startActivity(new Intent(getApplicationContext(), LeftTab.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.home: {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.about: {
                        return true;
                    }

                }
                return  false;
            }
        });

        init();
        ID = AccountId.getInstance().getAccount_id();

        setUpProfile(ID);
        setUpImage(ID);

        plus_icon.setOnClickListener(new View.OnClickListener() {       //프로필 이미지 변경
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        delete_move_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movetoDelete(ID);
            }
        });

    }

    private void init() {
        profile_img = (CircleImageView) findViewById(R.id.profile_img);
        plus_icon = (ImageButton) findViewById(R.id.plus_btn);
        show_username = (TextView) findViewById(R.id.show_username);
        show_id = (TextView) findViewById(R.id.show_id);
        show_level = (TextView) findViewById(R.id.show_level);
        delete_move_Btn = (AppCompatButton) findViewById(R.id.delete_move_Btn);
        trophy = (ImageView) findViewById(R.id.trophy);
        level = (TextView) findViewById(R.id.level);
        membership_rectangle = (RelativeLayout) findViewById(R.id.membership_rectangle);
        db = FirebaseFirestore.getInstance();
       // mStorageReference = FirebaseStorage.getInstance().getReference();
    }


    private void movetoDelete(String id) {
        Intent intent = new Intent(RightTab.this, DeleteAccountActivity.class);
        intent.putExtra("username", ID);
        startActivity(intent);
    }


    private void setUpProfile(String ID) {      //ID를 이용해서 Firestore 접근해서 정보 받아온다.
        db.collection("person")
                .document("customer")
                .collection("id")
                .document(ID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String membership=documentSnapshot.getString("membership");
                    String name=documentSnapshot.getString("name");
                    show_username.setText(name);
                    show_id.setText("@"+ID);
                    show_level.setText("#"+membership);
                    setUpMemberShip(membership);
                }else{
                    Toast.makeText(context, "Document not exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Document error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpMemberShip(String membership) {       //membership에 따라 색상 변경
        if(membership.equals("VIP")){
            trophy.setBackgroundResource(R.drawable.trophy_background2);
            level.setText(membership);
            membership_rectangle.setBackgroundResource(R.drawable.rectangle_7);
        }else{
            trophy.setBackgroundResource(R.drawable.trophy_background);
            level.setText(membership);
            membership_rectangle.setBackgroundResource(R.drawable.rectangle_5);
        }
    }

    private void setUpImage(String ID) {        //firebase storage에서 이미지 받아온다
        String path=ID+"_photo"+".jpg";
        Log.e("Here", "HHH...");
        mStorageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              // if (uri != null)
                Glide.with(RightTab.this).load(uri).into(profile_img);
                Log.e("Setup Image : ", "Suc    []");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "Picture Failed", Toast.LENGTH_SHORT).show();
                //Log.e("Setup Image : ", "[]    fail");
            }
        });
    }

    private void selectImage(){     //이미지 갤러리에서 가져옴
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                imageUri=data.getData();
                profile_img.setImageURI(imageUri);
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {        //가져온 이미지 firestore에 업로드
        String path=ID+"_photo"+".jpg";
        StorageReference fileRef=mStorageReference.child(path);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UpdateImageUrl(ID, path);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateImageUrl(String ID, String path) {         //업로드한 이미지 path firebase에 profile_img에 업데이트
        db.collection("person")
                .document("customer")
                .collection("id")
                .document(ID).update("profile_img", "gs://share-c4a93.appspot.com/"+path).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Update Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Update Path Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
