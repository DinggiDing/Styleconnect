package com.example.styleconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginAccountActivity extends AppCompatActivity {
    private EditText ID_ET, password_ET;
    private AppCompatButton signup_Btn, forgot_Btn, login_Btn;
    private FirebaseFirestore db;
    private CollectionReference documentRef;
    private final boolean isSuccess[] = {false};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db= FirebaseFirestore.getInstance();
        ID_ET=findViewById(R.id.ID_EditText);
        password_ET=findViewById(R.id.password_EditText);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        login_Btn=findViewById(R.id.login_Btn);     //로그인 버튼 클릭 시
        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID=ID_ET.getText().toString();
                String password=password_ET.getText().toString();
                checkLogin(ID, password);
            }
        });

        signup_Btn=findViewById(R.id.signup_Btn);       //회원가입 진행
        signup_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        forgot_Btn=findViewById(R.id.forgot_Btn);       //비밀번호 변경
        forgot_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLogin(String ID, String password) {     //로그인 진행
        isSuccess[0]=false;
        documentRef=db.collection("person")
                .document("customer")
                .collection("id");
        documentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){        //파이어스토어 아이디 중에서 검색한다.
                    if(ID.equals(documentSnapshot.getId())){
                        String original_password=documentSnapshot.getString("password");
                        if(original_password.equals(password)){         //비밀번호 일치 시 로그인 성공
                            isSuccess[0]=true;
                            Toast.makeText(LoginAccountActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            AccountId.getInstance().setAccount_id(ID);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //intent.putExtra("username", ID);
                            startActivity(intent);
                            //finish();

                            /*
                            Intent intent = new Intent(getApplicationContext(), DeleteAccountActivity.class);       //delete 배치하기 모호해서 다음 페이지로 배치 해 놓음
                            intent.putExtra("username", ID);
                            startActivity(intent);*/
                        }
                    }
                }
                if(!isSuccess[0]) {     //로그인 안 될 시 로그인 실패
                    Toast.makeText(LoginAccountActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
