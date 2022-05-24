package com.example.styleconnect;

import static com.example.styleconnect.R.drawable.trophy_background;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CodiAdapter extends RecyclerView.Adapter<CodiAdapter.ViewHolder> {

    private List<CodiItem> mDataList;    // item들의 데이터 저장 공간
    private StorageReference mStorageReference;
    private Context context;
    private String ID;

    public CodiAdapter(List<CodiItem> dataList, String ID) {
        this.ID = ID;
        mDataList = dataList;
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
        mStorageReference = storage.getReference();
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

        getClothesImage(item.getCodi_url(), holder.codi_img);
        //getClothesImage(item.getDesi_url(), holder.desi_img);
    }

    @Override
    public int getItemCount() {  // 아이템 개수
        return mDataList.size();
    }


    private void getClothesImage(String path, ImageView image) {      //사진 가져와서 imageview에 넣는 부분

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView desi_ID;
        TextView like_num;
        TextView category;
        CircleImageView desi_img;
        ImageView codi_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            desi_ID = itemView.findViewById(R.id.desi_profile_ID);
            like_num = itemView.findViewById(R.id.desi_profile_like);
            category = itemView.findViewById(R.id.codi_category);
            desi_img = itemView.findViewById(R.id.desi_profile_img);
            codi_img = itemView.findViewById(R.id.codi_image);
            desi_img.setImageResource(trophy_background);
        }
    }

}
