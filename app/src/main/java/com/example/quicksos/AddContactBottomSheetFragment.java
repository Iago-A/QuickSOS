package com.example.quicksos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactBottomSheetFragment extends BottomSheetDialogFragment {
    // Establish an interface to link with main fragment
    public interface AddContactListener {
        void onContactAdded(PersonalContact personalContact);
    }

    private AddContactListener listener;
    private EditText nameEditText;
    private EditText numberEditText;
    private EditText urlEditText;

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

        nameEditText = view.findViewById(R.id.nameEditText);
        numberEditText = view.findViewById(R.id.numberEditText);
        urlEditText = view.findViewById(R.id.urlEditText);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> saveContact());
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
}
