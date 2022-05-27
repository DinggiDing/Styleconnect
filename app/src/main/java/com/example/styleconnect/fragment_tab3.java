package com.example.styleconnect;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
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

public class fragment_tab3 extends Fragment {

    ArrayList<allele> list;
    albumListAdapter mAdapter;
    FirebaseFirestore db;
    private String ID;

    public fragment_tab3() {

    }

    public static fragment_tab3 newInstance() {
        fragment_tab3 fragment = new fragment_tab3();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View myfragmentView = inflater.inflate(R.layout.fragment_tab3, container, false);
        db = FirebaseFirestore.getInstance();

        GridView gridView = (GridView) myfragmentView.findViewById(R.id.gridView);
        ID = AccountId.getInstance().getAccount_id();


        list = new ArrayList<>();
        mAdapter = new albumListAdapter(getActivity(), R.layout.album_items, list);
        gridView.setAdapter(mAdapter);

        db.collection("person").document("customer").
                collection("id").document(ID).
                collection("closet").whereEqualTo("clo_item", "외투").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int id = 0;
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                id++;
                                String image = document.get("clo_img").toString();
                                list.add(new allele(image, id));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });



        return myfragmentView;
    }
}
