package imjimmyxd.simi.mywhatsapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import imjimmyxd.simi.mywhatsapp.user.UserListAdapter;
import imjimmyxd.simi.mywhatsapp.user.UserObject;
import imjimmyxd.simi.mywhatsapp.utils.CountryToPhonePrefix;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView.Adapter mUserListAdapter;

    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getContactList() {
        String ISOPrefix = getCountryISO();
        @SuppressLint("Recycle") Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = name.replace(" ", "");
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+")) phone = ISOPrefix + phone;

            UserObject mContacts = new UserObject("", name, phone);
            boolean found = contactList.stream().anyMatch(p -> p.getName().equals(mContacts.getName()));
            if (!found) {
                contactList.add(mContacts);
            }
            getUserDetails(mContacts);
        }
    }

    private void getUserDetails(UserObject mContacts) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContacts.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phone = "", name = "";
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) {
                            phone = Objects.requireNonNull(childSnapshot.child("phone").getValue()).toString();
                        }
                        if (childSnapshot.child("name").getValue() != null) {
                            name = Objects.requireNonNull(childSnapshot.child("name").getValue()).toString();
                        }
                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone)) {
                            for (UserObject p : contactList) {
                                if (p.getPhone().equals(mUser.getName())) {
                                    mUser.setName(p.getName());
                                    break;
                                }
                            }
                        }
                        userList.add(mUser);
//                        mUserListAdapter.notifyDataSetChanged();
//                        mUserListAdapter.notifyItemRangeInserted(userList.size() - 1, userList.size());
                        mUserListAdapter.notifyItemChanged(userList.size());
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (manager.getNetworkCountryIso() != null) {
            if (!manager.getNetworkCountryIso().equals("")) {
                iso = manager.getNetworkCountryIso();
            }
        }

        assert iso != null;
        return CountryToPhonePrefix.getPhone(iso);
    }

    private void initializeRecyclerView() {
        RecyclerView mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        RecyclerView.LayoutManager mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}