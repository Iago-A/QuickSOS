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

public class PersonalContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PersonalContactsAdapter adapter;
    private ArrayList<PersonalContact> personalContactList;

    public PersonalContactsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_personal_contacts, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        personalContactList = new ArrayList<>();
        adapter = new PersonalContactsAdapter(personalContactList);
        recyclerView.setAdapter(adapter);

        loadPersonalContactsFromFirebase();

        return view;
    }

    private void loadPersonalContactsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("emergencyContacts/user");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personalContactList.clear(); // Clear if it is necessary

                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    PersonalContact personalContact = contactSnapshot.getValue(PersonalContact.class);

                    if (personalContact != null) {
                        personalContactList.add(personalContact);
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
