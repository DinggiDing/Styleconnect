package com.example.styleconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class LeftTab extends AppCompatActivity {

    private Context context;
    private View view;

    private TextView codi, casual_bt, sports_bt, office_bt;
    private View dot1;
    private ImageButton test_profile_like_icon;

    private RecyclerView R_codi_list;    // 코디 출력 리사이클러 뷰

    private String ID;           // 사용자 ID
    private String Clothes_Type; // 카테고리
    private String desi_ID;      // 디자이너 ID

    // storage까지 경로
    private String c_fullpass;
    private String s_fullpass;
    private String o_fullpass;
    // 카테고리별 Codiitem 저장
    private List<CodiItem> codi_items = new ArrayList<>();
    private CodiAdapter codi_adapter;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
    StorageReference mStorageReference = storage.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lefttab);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    case R.id.dashboard: {
                        return true;
                    }

                    case R.id.home: {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.about: {
                        startActivity(new Intent(getApplicationContext(), RightTab.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                }
                return  false;
            }
        });

        init();
        ID = AccountId.getInstance().getAccount_id();

        // fullpass 경로 설정
        c_fullpass = "디자이너_코디/"+ID+"/casual";
        s_fullpass = "디자이너_코디/"+ID+"/sports";
        o_fullpass = "디자이너_코디/"+ID+"/office";

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        R_codi_list.setLayoutManager(layoutManager);
        R_codi_list.addItemDecoration(new RecyclerViewDecoration(30));


        // 텍스트 버튼 리스너 등록
        OnClickListener(casual_bt);
        OnClickListener(sports_bt);
        OnClickListener(office_bt);

        // 각 카테고리 폴더에서 모든 이미지 추출 후 각 리스트에 저장
        ListUp(c_fullpass, codi_items);

        // 카테고리별 CodiItem들 저장
        codi_adapter = new CodiAdapter(codi_items, ID);
        R_codi_list.setAdapter(codi_adapter);

    }

    public void OnClickListener(TextView button){   // 카테고리 클릭 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnClick(view);
            }
        });
    }

    public void BtnClick(View view){    // 카테고리 버튼 클릭 동작

        casual_bt.setTypeface(Typeface.DEFAULT);
        sports_bt.setTypeface(Typeface.DEFAULT);
        office_bt.setTypeface(Typeface.DEFAULT);
        switch(view.getId()){
            case R.id.Casual:
                casual_bt.setTypeface(Typeface.DEFAULT_BOLD);
                ListUp(c_fullpass, codi_items);

                break;
            case R.id.Sports:
                sports_bt.setTypeface(Typeface.DEFAULT_BOLD);
                ListUp(s_fullpass, codi_items);
                break;
            case R.id.Office:
                office_bt.setTypeface(Typeface.DEFAULT_BOLD);
                ListUp(o_fullpass, codi_items);
                break;
        }
    }

    // 경로에 있는 모든 이미지 가져오기
    private void ListUp(String full_path, List<CodiItem> list) {
        mStorageReference.child(full_path).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                list.clear();
                for (StorageReference item : listResult.getItems()) {

                    CodiItem codiItem = new CodiItem();
                    String[] temp = item.getName().split("_");
                    ID = temp[0];
                    Clothes_Type = temp[1];
                    desi_ID = temp[2].replace(".jpg", "");
                    codiItem.setCodi_url(full_path+"/"+item.getName());
                    codiItem.setCategory(Clothes_Type);
                    codiItem.setDesi_url(desi_ID+"_photo.jpg");
                    codiItem.setDesi_ID(desi_ID);

                    list.add(codiItem);  // 해당 카테고리에 CodiItem 추가
                }
                codi_adapter.notifyDataSetChanged();   // 리사이클러뷰 새로고침
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "list get failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {   // 리사이클러뷰 아이템간 상하 간격 설정
        private final int divHeight;
        public RecyclerViewDecoration(int divHeight){this.divHeight = divHeight;}

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = divHeight;
        }
    }

    private void init() {
        codi = (TextView) findViewById(R.id.codi_main);
        dot1 = (View) findViewById(R.id.dot1);
        casual_bt = (TextView) findViewById(R.id.Casual);
        casual_bt.setTypeface(Typeface.DEFAULT_BOLD);
        sports_bt = (TextView) findViewById(R.id.Sports);
        office_bt = (TextView) findViewById(R.id.Office);
        R_codi_list = (RecyclerView) findViewById(R.id.itemView);
    }
}
