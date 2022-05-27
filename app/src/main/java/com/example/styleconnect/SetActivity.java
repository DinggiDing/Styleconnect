package com.example.styleconnect;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetActivity extends AppCompatActivity {

    final int REQUEST_CODE_GALLERY = 999;
    private String path = null;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    ImageView imageView, btnChoose;
    Button btnAdd;
    Button radio_1, radio_2, radio_3, radio_4;
    TextView textupdown;
    EditText imagename;
    int img_id = 0;
    String radio_id = "상의";
    String image_name = "";
    private String ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        init();
        ID = AccountId.getInstance().getAccount_id();

        invisible(0);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        SetActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });


        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        GradientDrawable drawable = (GradientDrawable) getApplicationContext().getDrawable(R.drawable.background_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);




        getRadio(radio_1, radio_2, radio_3, radio_4);
        getRadio(radio_2, radio_1, radio_3, radio_4);
        getRadio(radio_3, radio_2, radio_1, radio_4);
        getRadio(radio_4, radio_2, radio_3, radio_1);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_name = String.valueOf(imagename.getText());
                Map<String, Object> clo = new HashMap<>();
                clo.put("clo_img", path);
                clo.put("clo_item", radio_id);
                clo.put("clo_name", image_name);
                db.collection("person").document("customer").
                        collection("id").document(ID).
                        collection("closet").add(clo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.e(TAG, "Document add");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Add Failed");
                            }
                        });
                invisible(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            invisible(1);
            Uri uri = data.getData();
            //path = getPathFromURI(uri);
            Glide.with(this).load(uri).into(imageView);
            StorageReference storageRef = storage.getReference();
            String img_path = ID + "_" + getTime() + ".png";
            path = img_path;
            StorageReference imageRef = storageRef.child(img_path);
            UploadTask uploadTask = imageRef.putFile(uri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetActivity.this, "사진이 업로드되지 않았습니다.", Toast.LENGTH_SHORT).show();

                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if(focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if(!rect.contains(x,y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void getRadio(Button radio, Button radio2, Button radio3, Button radio4) {
        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio.setBackground(getApplicationContext().getDrawable(R.drawable.button_radio_left));
                radio2.setBackground(getApplicationContext().getDrawable(R.drawable.button_radio_center));
                radio3.setBackground(getApplicationContext().getDrawable(R.drawable.button_radio_center));
                radio4.setBackground(getApplicationContext().getDrawable(R.drawable.button_radio_center));
                switch(view.getId()) {
                    case R.id.radio_type1:
                        radio_id = "상의";
                        break;
                    case R.id.radio_type2:
                        radio_id = "하의";
                        break;
                    case R.id.radio_type3:
                        radio_id = "외투";
                        break;
                    case R.id.radio_type4:
                        radio_id = "기타";
                        break;

                }
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_hh-mm-ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    private void invisible(int visible) {
        switch (visible) {
            case 0:
                imageView.setVisibility(View.GONE);
                radio_1.setVisibility(View.GONE);
                radio_2.setVisibility(View.GONE);
                radio_3.setVisibility(View.GONE);
                radio_4.setVisibility(View.GONE);
                textupdown.setVisibility(View.GONE);
                imagename.setVisibility(View.GONE);
                btnChoose.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);
                break;
            case 1:
                imageView.setVisibility(View.VISIBLE);
                radio_1.setVisibility(View.VISIBLE);
                radio_2.setVisibility(View.VISIBLE);
                radio_3.setVisibility(View.VISIBLE);
                radio_4.setVisibility(View.VISIBLE);
                textupdown.setVisibility(View.VISIBLE);
                imagename.setVisibility(View.VISIBLE);
                imagename.setHint("옷 이름을 입력해주세요");
                btnChoose.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setBackground(getDrawable(R.drawable.set_save_button_on));
                btnAdd.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void init() {
        textupdown = (TextView) findViewById(R.id.textupdown);
        imagename = (EditText) findViewById(R.id.image_name);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnChoose = (ImageView) findViewById(R.id.image_plus);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        radio_1 = (Button) findViewById(R.id.radio_type1);
        radio_2 = (Button) findViewById(R.id.radio_type2);
        radio_3 = (Button) findViewById(R.id.radio_type3);
        radio_4 = (Button) findViewById(R.id.radio_type4);

    }
}
