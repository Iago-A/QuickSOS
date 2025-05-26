package com.example.quicksos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class AddContactBottomSheetFragment extends BottomSheetDialogFragment {
    // Establish an interface to link with main fragment
    public interface AddContactListener {
        void onContactAdded(PersonalContact personalContact);
        void onContactUpdated(PersonalContact personalContact);
    }

    private AddContactListener listener;
    private EditText nameEditText;
    private EditText numberEditText;
    private EditText urlEditText;
    private TextView titleTextView;
    private Button saveButton;

    // Variables for edit mode
    private boolean isEditMode = false;
    private PersonalContact contactToEdit;
    private String originalName;
    private String originalNumber;

    // Static method to create instance for adding new contact
    public static AddContactBottomSheetFragment newInstanceForAdd() {
        return new AddContactBottomSheetFragment();
    }

    // Static method to create instance for editing contact
    public static AddContactBottomSheetFragment newInstanceForEdit(PersonalContact contact) {
        AddContactBottomSheetFragment fragment = new AddContactBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("name", contact.getName());
        args.putString("number", contact.getNumber());
        args.putString("url", contact.getUrl());
        args.putBoolean("isEditMode", true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we're in edit mode
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("isEditMode", false);
            if (isEditMode) {
                String name = getArguments().getString("name", "");
                String number = getArguments().getString("number", "");
                String url = getArguments().getString("url", "");
                contactToEdit = new PersonalContact(name, number, url);
                originalName = name;
                originalNumber = number;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Tries to obtain the reference for the listener
            if (getParentFragment() instanceof AddContactListener) {
                listener = (AddContactListener) getParentFragment();
            } else if (context instanceof AddContactListener) {
                listener = (AddContactListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("El padre debe implementar AddContactListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment_add_contact, container, false);

        titleTextView = view.findViewById(R.id.titleTextView);
        nameEditText = view.findViewById(R.id.nameEditText);
        numberEditText = view.findViewById(R.id.numberEditText);
        urlEditText = view.findViewById(R.id.urlEditText);
        saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        // Configure UI based on mode
        if (isEditMode) {
            titleTextView.setText(R.string.edit_contact);
            saveButton.setText(R.string.update);

            // Pre-fill fields with existing data
            nameEditText.setText(contactToEdit.getName());
            numberEditText.setText(contactToEdit.getNumber());
            urlEditText.setText(contactToEdit.getUrl());
        } else {
            titleTextView.setText(R.string.add_new_contact);
            saveButton.setText(R.string.save);
        }

        saveButton.setOnClickListener(v -> {
            if (isEditMode) {
                updateContact();
            } else {
                saveContact();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void saveContact() {
        String name = nameEditText.getText().toString().trim();
        String number = numberEditText.getText().toString().trim();
        String url = urlEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError(getString(R.string.name_required));
            return;
        }

        if (number.isEmpty()) {
            numberEditText.setError(getString(R.string.number_required));
            return;
        }

        // If url is empty use a predeterminate one
        if (url.isEmpty()) {
            url = "https://static-00.iconduck.com/assets.00/contacts-icon-2048x2048-ljihie2i.png";
        }

        // Create contact
        PersonalContact personalContact = new PersonalContact(name, number, url);

        // Save in Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "user";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference contactsRef = database.getReference("emergencyContacts")
                .child("user")
                .child(userId);

        String contactId = contactsRef.push().getKey();

        if (contactId != null) {
            contactsRef.child(contactId).setValue(personalContact)
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onContactAdded(personalContact);
                        }
                        Toast.makeText(getContext(), getString(R.string.successful_contact_added), Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), getString(R.string.failed_contact_added), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateContact() {
        String name = nameEditText.getText().toString().trim();
        String number = numberEditText.getText().toString().trim();
        String url = urlEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError(getString(R.string.name_required));
            return;
        }

        if (number.isEmpty()) {
            numberEditText.setError(getString(R.string.number_required));
            return;
        }

        // If url is empty use a predeterminate one
        if (url.isEmpty()) {
            url = "https://static-00.iconduck.com/assets.00/contacts-icon-2048x2048-ljihie2i.png";
        }

        // Create updated contact
        PersonalContact updatedContact = new PersonalContact(name, number, url);

        // Find and update the contact in Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "user";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference contactsRef = database.getReference("emergencyContacts")
                .child("user")
                .child(userId);

        // Find the contact by matching original name and number
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    PersonalContact contact = contactSnapshot.getValue(PersonalContact.class);

                    if (contact != null &&
                            contact.getName().equals(originalName) &&
                            contact.getNumber().equals(originalNumber)) {

                        // Update the contact
                        contactSnapshot.getRef().setValue(updatedContact)
                                .addOnSuccessListener(aVoid -> {
                                    if (listener != null) {
                                        listener.onContactUpdated(updatedContact);
                                    }
                                    Toast.makeText(getContext(), getString(R.string.successful_contact_updated), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), getString(R.string.failed_contact_updated), Toast.LENGTH_SHORT).show();
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getString(R.string.failed_contact_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }
}