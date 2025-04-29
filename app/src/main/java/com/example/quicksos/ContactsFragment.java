package com.example.quicksos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private ArrayList<Contact> contactList;

    public ContactsFragment () {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contactList = new ArrayList<>();
        adapter = new ContactsAdapter(contactList);
        recyclerView.setAdapter(adapter);

        loadContactsFromFirebase();

        return view;
    }

    private void loadContactsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("emergencyContacts/default");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear(); // Clear if it is necessary

                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);

                    if (contact != null) {
                        contactList.add(contact);
                    }
                }

                adapter.notifyDataSetChanged(); // Refresh RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // I can place a toast to show the error
            }
        });
    }
}
