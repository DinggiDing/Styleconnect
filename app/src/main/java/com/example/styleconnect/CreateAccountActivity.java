package com.example.styleconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText create_ID_ET, create_username_ET, create_password_ET, create_confirm_ET;
    private AppCompatButton create_account_Btn;
    private FirebaseFirestore db;
    private CollectionReference documentRef;
    final boolean isSuccess[] = {false};
    private String path;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
    StorageReference mStorageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        db=FirebaseFirestore.getInstance();
        create_ID_ET=findViewById(R.id.create_ID_EditText);
        create_username_ET=findViewById(R.id.create_username_EditText);
        create_password_ET=findViewById(R.id.create_password_EditText);
        create_confirm_ET=findViewById(R.id.create_confirm_EditText);
        create_account_Btn=findViewById(R.id.create_account_Btn);

        create_account_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID=create_ID_ET.getText().toString();
                String username=create_username_ET.getText().toString();
                String password=create_password_ET.getText().toString();
                String conf_password=create_confirm_ET.getText().toString();
                String member_level="family";

                uniqueID(ID, new GetResponseCallback(){
                    @Override
                    public void onCallback(Boolean cond) {      //콜백 함수 이용 ->ID가 유일 할 시 -> 등록 시작
                        if(!cond){
                            if(ValidatePassword(password)){
                                if(password.equals(conf_password)) {
                                    path=ID+"_photo"+".jpg";
                                    String profile_url="gs://share-c4a93.appspot.com/"+path;
                                    Uri path_uri = Uri.parse("android.resource://com.example.styleconnect/"+R.drawable.img);
                                    uploadImage(path_uri, ID);
                                    UpdateData(ID, member_level, username, password, profile_url);      //ID가 유일하고 비밀번호가 맞기 때문에 본격적인 등록 시작
                                }else{
                                    Toast.makeText(CreateAccountActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CreateAccountActivity.this, "Password: 8~15 character", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    public interface GetResponseCallback {
        void onCallback(Boolean cond);
    }
    private void uniqueID(String ID, final GetResponseCallback callback) {
        isSuccess[0]=false;
        documentRef=db.collection("person")
                .document("customer")
                .collection("id");
        documentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    if(ID.equals(documentSnapshot.getId())){          //ID가 존재 할 시
                        isSuccess[0]=true;
                        Toast.makeText(CreateAccountActivity.this, "ID not unique", Toast.LENGTH_SHORT).show();
                    }
                }
                callback.onCallback(isSuccess[0]);
            }
        });
    }

    private boolean ValidatePassword(String password){      //비밀번호 정규식 맞는지 (정규식: 영문자+숫자 (8~15자리))
        String pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9]).{8,15}$";
        Boolean matches= Pattern.matches(pwPattern, password);
        if(matches){
            return true;
        }else{
            return false;
        }
    }

    private void UpdateData(String ID, String member_level, String username, String password, String profile_url) {        //파이어 스토어에 등록
        Map<String, Object> user = new HashMap<>();
        user.put("membership", member_level);
        user.put("name", username);
        user.put("password", password);
        user.put("profile_img", profile_url);
        db.collection("person")
                .document("customer")
                .collection("id")
                .document(ID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreateAccountActivity.this, "Create Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginAccountActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateAccountActivity.this, "Create Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri imageUri, String ID) {        //가져온 이미지 firestore에 업로드
        path=ID+"_photo"+".jpg";
        StorageReference fileRef=mStorageReference.child(path);
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateAccountActivity.this, "사진이 업로드되지 않았습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
