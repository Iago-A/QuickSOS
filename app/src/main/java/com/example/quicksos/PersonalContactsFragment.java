package com.example.quicksos;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PersonalContactsFragment extends Fragment implements AddContactBottomSheetFragment.AddContactListener {
    private RecyclerView recyclerView;
    private PersonalContactsAdapter adapter;
    private ArrayList<PersonalContact> personalContactList;
    private DatabaseReference reference;
    private String userId;
    private TextView deleteTextView;
    private TextView updateTextView;

    public PersonalContactsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_personal_contacts, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "userID";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("emergencyContacts/user/" + userId);

        recyclerView = view.findViewById(R.id.recycler_view_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        deleteTextView = view.findViewById(R.id.deleteTextView);
        updateTextView = view.findViewById(R.id.updateTextView);

        personalContactList = new ArrayList<>();
        adapter = new PersonalContactsAdapter(personalContactList, this::onContactLongPressed);
        recyclerView.setAdapter(adapter);

        setupSwipeToDelete();

        loadPersonalContactsFromFirebase();

        return view;
    }

    private void onContactLongPressed(PersonalContact contact) {
        AddContactBottomSheetFragment bottomSheet = AddContactBottomSheetFragment.newInstanceForEdit(contact);
        bottomSheet.show(getChildFragmentManager(), "EditContactBottomSheet");
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't implement drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Verify that the item is not the last one (add button)
                if (position < personalContactList.size()) {
                    PersonalContact contactToDelete = personalContactList.get(position);

                    // Show confirmation dialog
                    new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.delete_contact_title)
                            .setMessage(getString(R.string.delete_contact_message) + " " + contactToDelete.getName() + "?")
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                deleteContact(position);
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                // If user cancels, restore the item in the list
                                adapter.notifyItemChanged(position);
                            })
                            .setOnCancelListener(dialog -> {
                                // Also restore if dialog is dismissed
                                adapter.notifyItemChanged(position);
                            })
                            .show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                if (viewHolder instanceof PersonalContactsAdapter.ViewHolder) {
                    View itemView = viewHolder.itemView;

                    // Calculate opacity based on how far the user has swiped
                    // 0 = completely transparent, 255 = completely opaque
                    float width = itemView.getWidth();
                    float alpha = Math.min(1f, dX / (width * 0.5f)); // Alcanza opacidad completa a la mitad del deslizamiento

                    if (alpha > 0) {
                        // Red background with calculated alpha
                        Paint paint = new Paint();
                        int color = ContextCompat.getColor(requireContext(), R.color.colorDelete);
                        paint.setColor(color);
                        paint.setAlpha((int) (alpha * 255)); // Set transparency

                        // Draw background
                        c.drawRect(
                                itemView.getLeft(),
                                itemView.getTop(),
                                itemView.getLeft() + dX,
                                itemView.getBottom(),
                                paint
                        );

                        // Draw delete icon with calculated alpha
                        Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24);
                        if (deleteIcon != null) {
                            deleteIcon.setAlpha((int) (alpha * 255)); // Set icon transparency

                            int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                            int iconTop = itemView.getTop() + iconMargin;
                            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                            int iconLeft = itemView.getLeft() + iconMargin;
                            int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            deleteIcon.draw(c);
                        }
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            // Prevent swiping for add button (the last item)
            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof PersonalContactsAdapter.AddButtonViewHolder) {
                    return 0; // Disable swiping for add button
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        // Attach the helper to the RecyclerView
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    private void deleteContact(int position) {
        if (position >= 0 && position < personalContactList.size()) {
            PersonalContact contactToDelete = personalContactList.get(position);

            // We need to find the contact's ID in Firebase
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Iterate through all children to find the one matching our contact
                    for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                        PersonalContact contact = contactSnapshot.getValue(PersonalContact.class);

                        if (contact != null &&
                                contact.getName().equals(contactToDelete.getName()) &&
                                contact.getNumber().equals(contactToDelete.getNumber())) {

                            // We found the contact, now delete it using its key
                            contactSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), R.string.successfully_contact_deleted, Toast.LENGTH_SHORT).show();
                                        // The contact list will update automatically through the ValueEventListener
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), R.string.failed_contact_deleted, Toast.LENGTH_SHORT).show();
                                    });

                            break; // Exit loop after finding and deleting
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), R.string.failed_contact_deleted, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadPersonalContactsFromFirebase() {
        reference.addValueEventListener(new ValueEventListener() {
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

                updateInstructionsVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), getString(R.string.error_loading_contacts), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateInstructionsVisibility() {
        if (personalContactList.size() > 0) {
            deleteTextView.setVisibility(View.VISIBLE);
            updateTextView.setVisibility(View.VISIBLE);
        } else {
            deleteTextView.setVisibility(View.GONE);
            updateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onContactAdded(PersonalContact personalContact) {
        // The contacts list will be updated automatically by the ValueEventListener
        // We don't need to do anything specific here
    }

    @Override
    public void onContactUpdated(PersonalContact personalContact) {
        // The contacts list will be updated automatically by the ValueEventListener
        // We don't need to do anything specific here
    }
}