package imjimmyxd.simi.mywhatsapp.chat;


import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.ListIterator;

import imjimmyxd.simi.mywhatsapp.R;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    ArrayList<MessageObject> messageList = new ArrayList<>();

    public MessageAdapter(ArrayList<MessageObject> messageList) {
        this.messageList = messageList;
    }

    public ArrayList<MessageObject> getMessage() {
        return messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new MessageAdapter.MessageViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());
        if (messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()) {
            holder.mViewMedia.setVisibility(View.GONE);
        }
        holder.mViewMedia.setOnClickListener(view -> new StfalconImageViewer.Builder<String>(view.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList(), (imageView, image) -> {
            //TODO keep Glide for one package or Picassio because it feels smoother but idk if its realy smoother

//                        Glide.with(view.getContext()).load(image).into(imageView);
            Picasso.get().load(image).into(imageView);
        }).show());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mMessage,
                mSender;
        LinearLayout mLayout;
        Button mViewMedia;

        MessageViewHolder(View view) {
            super(view);
            mMessage = view.findViewById(R.id.messageList);
            mSender = view.findViewById(R.id.sender);
            mLayout = view.findViewById(R.id.layout);
            mViewMedia = view.findViewById(R.id.viewMedia);
        }
    }
}
