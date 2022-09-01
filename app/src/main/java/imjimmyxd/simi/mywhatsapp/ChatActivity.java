package imjimmyxd.simi.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import imjimmyxd.simi.mywhatsapp.chat.MediaAdapter;
import imjimmyxd.simi.mywhatsapp.chat.MessageAdapter;
import imjimmyxd.simi.mywhatsapp.chat.MessageObject;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView mChat, mMedia;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;
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
        Button mAddMedia = findViewById(R.id.addMedia);
        mSend.setOnClickListener(view -> {
            sendMessage();
        });
        mAddMedia.setOnClickListener(view -> {
            openGallery();
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList;

    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (Objects.requireNonNull(data).getClipData() == null) {
                    mediaUriList.add(Objects.requireNonNull(data).getData().toString());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getChatMessages() {
        mChatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String text = "",
                            creatorID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if (snapshot.child("text").getValue() != null) {
                        text = Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                    }
                    if (snapshot.child("creator").getValue() != null) {
                        creatorID = Objects.requireNonNull(snapshot.child("creator").getValue()).toString();
                    }
                    if (snapshot.child("media").getChildrenCount() > 0) {
                        for(DataSnapshot mediaSnapshot : snapshot.child("media").getChildren()) {
                            mediaUrlList.add(Objects.requireNonNull(mediaSnapshot.getValue()).toString());
                        }
                    }
                    MessageObject mMessage = new MessageObject(snapshot.getKey(), creatorID, text,mediaUrlList);
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

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;

    private void sendMessage() {
        mMessage = findViewById(R.id.messageInput);
        String messageId = mChatDB.push().getKey();
        assert messageId != null;
        final DatabaseReference newMessageDB = mChatDB.child(messageId);
        final Map newMessageMap = new HashMap<>();
        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
        if (!mMessage.getText().toString().isEmpty())
            newMessageMap.put("text", mMessage.getText().toString());
        if (!mediaUriList.isEmpty()) {
            for (String mediaUri : mediaUriList) {
                String mediaId = newMessageDB.child("media").push().getKey();
                mediaIdList.add(mediaId);
                assert mediaId != null;
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(messageId).child(mediaId);
                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                totalMediaUploaded++;
                                if (totalMediaUploaded == mediaUriList.size()) {
                                    updateDatabaseWithNewMessages(newMessageDB, newMessageMap);
                                }
                            }
                        });
                    }
                });
            }
        } else {
            if (!mMessage.getText().toString().isEmpty())
                updateDatabaseWithNewMessages(newMessageDB, newMessageMap);
        }
    }

    private void updateDatabaseWithNewMessages(DatabaseReference newMessageDB, Map newMessageMap) {
        newMessageDB.updateChildren(newMessageMap);
        mMediaAdapter.notifyDataSetChanged();
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }
}