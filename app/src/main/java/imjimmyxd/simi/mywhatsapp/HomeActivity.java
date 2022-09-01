package imjimmyxd.simi.mywhatsapp;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import imjimmyxd.simi.mywhatsapp.chat.ChatListAdapter;
import imjimmyxd.simi.mywhatsapp.chat.ChatObject;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button logout, findUser;
    ImageView menu;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView mChatList;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //TODO not sure if Fresco.initialize() is a must
        Fresco.initialize(this);
        mAuth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Menu Pressed!", Toast.LENGTH_SHORT).show());


//        chatList.add(new ChatObject("test"));

        findUser = findViewById(R.id.find);
        findUser.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, FindUserActivity.class));
        });
        getPermissions();
        initializeRecyclerView();
        getUserChatList();
        System.out.println("debug");
    }

    private void getUserChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("chat");
        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        ChatObject mChat = new ChatObject(Objects.requireNonNull(childSnapshot.getKey()).toString());
                        boolean exists = false;
                        for (ChatObject mChatIterator : chatList) {
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList.add(mChat);
                        mChatListAdapter.notifyItemChanged(chatList.size());
//                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeRecyclerView() {
        RecyclerView mChatList = findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        RecyclerView.LayoutManager mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}