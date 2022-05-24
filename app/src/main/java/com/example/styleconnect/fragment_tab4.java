package com.example.styleconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class fragment_tab4 extends Fragment {

    ArrayList<allele> list;
    albumListAdapter mAdapter;
    FirebaseFirestore db;

    public fragment_tab4() {

    }

    public static fragment_tab4 newInstance() {
        fragment_tab4 fragment = new fragment_tab4();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View myfragmentView = inflater.inflate(R.layout.fragment_tab4, container, false);
        db = FirebaseFirestore.getInstance();

        GridView gridView = (GridView) myfragmentView.findViewById(R.id.gridView);

        list = new ArrayList<>();
        mAdapter = new albumListAdapter(getActivity(), R.layout.album_items, list);
        gridView.setAdapter(mAdapter);

        db.collection("person").document("customer").
                collection("id").document("jae7151").
                collection("closet").whereEqualTo("clo_item", "기타").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int id = 0;
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                id++;
                                String image = document.get("clo_img").toString();
                                list.add(new allele(image, id));
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                    }

                });



        return myfragmentView;
    }
}
