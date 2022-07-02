package com.example.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactFragment extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private DatabaseReference userRef,contactRef;
    private String currentUID;

    View contactView;

    public ContactFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactView=inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = contactView.findViewById(R.id.rvContactFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        currentUID = auth.getCurrentUser().getUid();


        return contactView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ContactViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull Contacts model) {
                String userId = getRef(position).getKey();

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Profile")){

                            String prof = snapshot.child("Profile").getValue().toString();
                            String name = snapshot.child("UserName").getValue().toString();
                            String status = snapshot.child("Status").getValue().toString();

                            holder.username.setText(name);
                            holder.status.setText(status);
                            Picasso.get().load(prof).placeholder(R.drawable.ic_baseline_person_24).into(holder.profile);

                        }
                        else{
                            String name = snapshot.child("UserName").getValue().toString();
                            String status = snapshot.child("Status").getValue().toString();

                            Picasso.get().load(R.drawable.ic_baseline_person_24).placeholder(R.drawable.ic_baseline_person_24).into(holder.profile);
                            holder.username.setText(name);
                            holder.status.setText(status);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                return new ContactViewHolder(v);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView status,username;
        CircleImageView profile;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            status =itemView.findViewById(R.id.txtUserStatusUsersDisplay);
            username =itemView.findViewById(R.id.txtUserNameUsersDisplay);
            profile =itemView.findViewById(R.id.picUsersDisplay);



        }
    }
}