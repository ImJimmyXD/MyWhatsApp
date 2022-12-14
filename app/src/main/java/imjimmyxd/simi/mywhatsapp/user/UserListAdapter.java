package imjimmyxd.simi.mywhatsapp.user;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import imjimmyxd.simi.mywhatsapp.R;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    ArrayList<UserObject> userList;

    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    public ArrayList<UserObject> getUserList() {
        return userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new UserListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
        holder.mLayout.setOnClickListener(v -> {
            String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
            assert key != null;
            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("chat").child(key).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).setValue(true);

//            ((Activity) v.getContext()).finish();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder {

        public TextView mName, mPhone;
        public LinearLayout mLayout;
        public UserListViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}
