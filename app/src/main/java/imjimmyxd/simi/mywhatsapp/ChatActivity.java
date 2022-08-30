package imjimmyxd.simi.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import imjimmyxd.simi.mywhatsapp.chat.MessageAdapter;
import imjimmyxd.simi.mywhatsapp.chat.MessageObject;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView mChat;
    private RecyclerView.LayoutManager mChatLayoutManager;

    //    ArrayList<ChatObject> messageList = new ArrayList<>();
    ArrayList<MessageObject> messageList;

    String chatID;
    DatabaseReference mChatDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");

        mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);

        Button mSend = findViewById(R.id.send);
        mSend.setOnClickListener(view -> {
            sendMessage();
        });

        initializeRecyclerView();
        getChatMessages();
    }

    private void getChatMessages() {
        mChatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String text = "",
                            creatorID = "";
                    if (snapshot.child("text").getValue() != null) {
                        text = Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                    }
                    if (snapshot.child("creator").getValue() != null) {
                        creatorID = Objects.requireNonNull(snapshot.child("creator").getValue()).toString();
                    }
                    MessageObject mMessage = new MessageObject(snapshot.getKey(), creatorID, text);
                    messageList.add(mMessage);
                    //TODO solve error from try-catch block
                    // W/System.err: java.lang.NullPointerException: Attempt to invoke virtual method 'void androidx.recyclerview.widget.RecyclerView$LayoutManager.scrollToPosition(int)' on a null object reference

                    try {
                        mChatLayoutManager.scrollToPosition(messageList.size() - 1);
                    } catch (Exception e) {
                        System.err.println(e);
                        System.out.println("WTF " + e);
                    }
//                    mChatAdapter.notifyDataSetChanged();
                    mChatAdapter.notifyItemChanged(messageList.size());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        EditText mMessage = findViewById(R.id.messageInput);
        if (!mMessage.getText().toString().isEmpty()) {
            DatabaseReference newMessageDB = mChatDB.push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            newMessageDB.updateChildren(newMessageMap);
        }
        mMessage.setText(null);
    }

    private void initializeRecyclerView() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        RecyclerView.LayoutManager mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }
}