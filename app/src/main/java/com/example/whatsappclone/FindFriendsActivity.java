package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.rvfindFriends);
        toolbar = findViewById(R.id.appbarfindFriends);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find friends");

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().
                setQuery(userRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,findFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, findFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findFriendsViewHolder holder, int position, @NonNull Contacts model) {

                holder.txtUserName.setText(model.getUserName());
                holder.txtStatus.setText(model.getStatus());
                Picasso.get().load(model.getProfile()).placeholder(R.drawable.ic_baseline_person_24).into(holder.profImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_User_ID = getRef(holder.getAdapterPosition()).getKey();

                        Intent i = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        i.putExtra("ID",visit_User_ID);
                        startActivity(i);


                    }
                });

            }

            @NonNull
            @Override
            public findFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                return new findFriendsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
    private static class findFriendsViewHolder extends RecyclerView.ViewHolder
            {
                TextView txtUserName,txtStatus;
                CircleImageView profImage;

                public findFriendsViewHolder(@NonNull View itemView) {
                    super(itemView);

                    txtStatus = itemView.findViewById(R.id.txtUserStatusUsersDisplay);
                    txtUserName = itemView.findViewById(R.id.txtUserNameUsersDisplay);
                    profImage = itemView.findViewById(R.id.picUsersDisplay);


                }
            }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}