package com.example.styleconnect;

import static com.example.styleconnect.R.drawable.trophy_background;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CodiAdapter extends RecyclerView.Adapter<CodiAdapter.ViewHolder> {


    private List<CodiItem> mDataList;    // item들의 데이터 저장 공간
    private StorageReference mStorageReference;
    private FirebaseFirestore db;
    private Context context;
    private String ID;
    private final Long numArr[] = {Long.valueOf(0)};

    // (수정 bj hj 5/27)
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public CodiAdapter(List<CodiItem> dataList, String ID) {
        this.ID = ID;
        mDataList = dataList;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
        mStorageReference = storage.getReference();
        db=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // view holder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frame, parent, false);  // itemframe가져오기

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {  // 데이터 할당
        context = holder.itemView.getContext();
        CodiItem item = mDataList.get(position);
        holder.category.setText(item.getCategory());
        holder.desi_ID.setText(item.getDesi_ID());
        Log.e("URL : ", item.getCodi_url());

        holder.like_bt.setOnClickListener(new View.OnClickListener() {    // 좋아요 버튼 클릭시 데이터 추가
            @Override
            public void onClick(View view) {
                DesignerLikeUpdate(ID, item.getDesi_ID(), 1, holder.like_num);
                getLikeNum(item.getDesi_ID(), holder.like_num);
            }
        });

        getLikeNum(item.getDesi_ID(), holder.like_num);  // 좋아요 개수 확인 기능 추가(by hj 5/24)
        getImage(item.getCodi_url(), holder.codi_img);
        getImage(item.getDesi_url(), holder.desi_img);
    }

    @Override
    public int getItemCount() {  // 아이템 개수
        return mDataList.size();
    }

    // 이미지 가져오기 함수명 변경(by hj 5/24)
    private void getImage(String path, ImageView image) {

        mStorageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (context != null) {
                    Glide.with(context).load(uri).into(image);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Picture Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 좋아요 개수 가저오기 함수 추가(by hj 5/24)
    private void getLikeNum(String designerId, TextView likeNum){
        db.collection("person")
                .document("designer")
                .collection("id")
                .document(designerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    long num=Long.parseLong(documentSnapshot.getString("like"));
                    String num_format=formatNumber(num);
                    Log.d("PPPTAG", num_format);
                    likeNum.setText(num_format);
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

    //좋아요 숫자 변경 함수 추가(by hj 5/24)
    private void DesignerLikeUpdate(String ID, String D_ID, int adder, TextView like_num) {
        DocumentReference documentReference = db.collection("person")
                .document("designer")
                .collection("id")
                .document(D_ID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    numArr[0] = Long.parseLong(documentSnapshot.getString("like"));
                    String num_format = formatNumber(numArr[0]);
                    numArr[0] += adder;
                    documentReference.update("like", String.valueOf(numArr[0]));
                } else {
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

    //숫자 단위 변경함수 추가(by hj 5/27)
    private static String formatNumber(long value) {

        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumber(Long.MIN_VALUE + 1);
        if (value < 0) return formatNumber(0);   //***: 수정부분 -1 ->0 으로
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView desi_ID;
        TextView like_num;
        TextView category;
        CircleImageView desi_img;
        ImageView codi_img;
        ImageButton like_bt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            desi_ID = itemView.findViewById(R.id.desi_profile_ID);
            like_num = itemView.findViewById(R.id.desi_profile_like);
            category = itemView.findViewById(R.id.codi_category);
            desi_img = itemView.findViewById(R.id.desi_profile_img);
            codi_img = itemView.findViewById(R.id.codi_image);
            desi_img.setImageResource(trophy_background);
            like_bt = itemView.findViewById(R.id.like_icon);
        }
    }

}
