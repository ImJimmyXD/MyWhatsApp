package imjimmyxd.simi.mywhatsapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import imjimmyxd.simi.mywhatsapp.R;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    ArrayList<ChatObject> ChatList = new ArrayList<>();

    public ChatListAdapter(ArrayList<ChatObject> ChatList) {
        this.ChatList = ChatList;
    }

    public ArrayList<ChatObject> getChatList() {
        return ChatList;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new ChatListAdapter.ChatListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        holder.mTitle.setText(ChatList.get(position).getChatId());

        holder.mLayout.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }


    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public LinearLayout mLayout;

        public ChatListViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.title);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}
