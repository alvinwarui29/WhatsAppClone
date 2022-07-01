package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupFragment extends Fragment {

    private View fragementView;
    private ListView listView;
    private DatabaseReference groupRef;
    private ArrayList<String > groupList = new ArrayList<>();
    private ArrayAdapter<String > adapter;




    public GroupFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragementView=inflater.inflate(R.layout.fragment_group, container, false);
        InitializeViews();
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        RetreiveAndDisplayGroups();

        return fragementView;

    }

    private void InitializeViews() {
        listView = fragementView.findViewById(R.id.listViewGroups);
        adapter = new ArrayAdapter<String >(getContext(), android.R.layout.simple_list_item_1,groupList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String grpName = parent.getItemAtPosition(position).toString();
                Intent i = new Intent(getContext(),GroupChatActivity.class);
                i.putExtra("grpName",grpName);
                startActivity(i);

            }
        });

    }

    private void RetreiveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String > set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();

                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                groupList.clear();
                groupList.addAll(set);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }





}