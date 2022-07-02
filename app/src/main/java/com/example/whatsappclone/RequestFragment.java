package com.example.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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

public class RequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private View requestview;
    private FirebaseAuth auth;
    private DatabaseReference chatRef,userRef;
    private String currnetUID;


    public RequestFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestview=inflater.inflate(R.layout.fragment_request, container, false);
        recyclerView = requestview.findViewById(R.id.rvRequestFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userRef =FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();
        currnetUID = auth.getCurrentUser().getUid().toString();
        chatRef = FirebaseDatabase.getInstance().getReference("Chat_Request");


        return requestview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatRef.child(currnetUID),Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Contacts model) {

                holder.itemView.findViewById(R.id.btnDeclineUserDisplay).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.btnAcceptUserDisplay).setVisibility(View.VISIBLE);

                String listUserID = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            String type = snapshot.getValue().toString();

                            if (type.equals("recieved")){
                                userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
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

                                            Picasso.get().load(R.drawable.ic_baseline_person_24).into(holder.profile);

                                            holder.username.setText(name);
                                            holder.status.setText(status);

                                        }

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                        }


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                return new RequestViewHolder(v);

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private class RequestViewHolder extends RecyclerView.ViewHolder{

        private TextView status,username;
        private CircleImageView profile;
        private AppCompatButton btnAccept,btnDecline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            status =itemView.findViewById(R.id.txtUserStatusUsersDisplay);
            username =itemView.findViewById(R.id.txtUserNameUsersDisplay);
            profile =itemView.findViewById(R.id.picUsersDisplay);
            btnAccept = itemView.findViewById(R.id.btnAcceptUserDisplay);
            btnDecline= itemView.findViewById(R.id.btnDeclineUserDisplay);




        }
    }


}