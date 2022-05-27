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

import de.hdodenhof.circleimageview.CircleImageView;

public class CodiAdapter extends RecyclerView.Adapter<CodiAdapter.ViewHolder> {

    private List<CodiItem> mDataList;    // item들의 데이터 저장 공간
    private StorageReference mStorageReference;
    private FirebaseFirestore db;
    private Context context;
    private String ID;
    private final Long numArr[] = {Long.valueOf(0)};

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

    //숫자 단위 변경함수 추가(by hj 5/24)
    private static String formatNumber(long count) {
        long real_num = count;
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp),"kMGTPE".charAt(exp-1));
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
