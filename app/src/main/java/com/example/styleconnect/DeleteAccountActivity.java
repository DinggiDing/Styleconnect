package com.example.styleconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountActivity extends AppCompatActivity {
    private EditText delete_ID_ET;
    private AppCompatButton delete_account_Btn;
    private FirebaseFirestore db;
    private StorageReference mStorageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);
        db= FirebaseFirestore.getInstance();
        mStorageReference= FirebaseStorage.getInstance().getReference();
        delete_ID_ET=findViewById(R.id.delete_ID_EditText);
        Intent intent=getIntent();
        String us_name=intent.getStringExtra("username");       //user name은 로그인 되어 있을 것이니 -> 바로 표시해 준다.
        delete_ID_ET.setText(us_name);
        delete_account_Btn=findViewById(R.id.delete_account_Btn);

        delete_account_Btn.setOnClickListener(new View.OnClickListener() {      //회원 삭제 진행
            @Override
            public void onClick(View view) {
                String username = delete_ID_ET.getText().toString();
                DeleteData(username);
            }
        });
    }

    private void DeleteData(String ID) {      //회원 ID 존재시 삭제
        db.collection("person")
                .document("customer")
                .collection("id")
                .document(ID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DeleteImage(ID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeleteAccountActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DeleteImage(String ID) {
        String path=ID+"_photo"+".jpg";
        StorageReference fileRef=mStorageReference.child(path);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DeleteAccountActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DeleteAccountActivity.this, "Delete Image Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
