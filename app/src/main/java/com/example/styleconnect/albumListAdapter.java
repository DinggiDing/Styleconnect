package com.example.styleconnect;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class albumListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<allele> albumList;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://share-c4a93.appspot.com");
    StorageReference storageRef = storage.getReference();

    public albumListAdapter(Context context, int layout, ArrayList<allele> albumList) {
        this.context = context;
        this.layout = layout;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
    }

    GradientDrawable drawable;

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.imageView = (ImageView) row.findViewById(R.id.imgDog);
            // drawable = (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
            //  holder.imageView.setBackground(drawable);
            holder.imageView.setClipToOutline(true);
            //Log.e("Adapter", albumList.get(position).getImage());

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        allele allele = albumList.get(position);
        ViewHolder finalHolder = holder;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH).dontAnimate().dontTransform();
        storageRef.child(allele.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri)
                        .apply(requestOptions).into(finalHolder.imageView);
            }
        });


        return row;
    }
}
