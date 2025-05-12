package com.example.quicksos;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class PersonalContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PersonalContact> personalContacts;

    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_ADD_BUTTON = 1;

    public PersonalContactsAdapter(ArrayList<PersonalContact> personalContacts) {
        this.personalContacts = personalContacts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_CONTACT) {
            View view = inflater.inflate(R.layout.recycler_view_cell, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.recycler_view_cell_add_contact, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            PersonalContact personalContact = personalContacts.get(position);
            ViewHolder contactHolder = (ViewHolder) holder;

            contactHolder.contactNameTextView.setText(personalContact.getName());
            contactHolder.contactNumberTextView.setText(personalContact.getNumber());

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(holder.itemView.getContext())
                    .load(personalContact.getUrl())
                    .apply(requestOptions)
                    .into(contactHolder.contactIconImageView);

            // Listener for write contact number directly in the phone
            String finalContactName = personalContact.getName();
            holder.itemView.setOnClickListener(v -> {
                String phoneNumber = personalContact.getNumber();

                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.call_confirmation_title)
                        .setMessage(v.getContext().getString(R.string.call_question) + " " + finalContactName + "?")
                        .setPositiveButton(R.string.accept_call, (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phoneNumber));
                            v.getContext().startActivity(intent);
                        })
                        .setNegativeButton(R.string.cancel_call, null)
                        .show();
            });
        } else {
            if (holder instanceof AddButtonViewHolder) {
                holder.itemView.setOnClickListener(v -> {
                    FragmentActivity activity = (FragmentActivity) v.getContext();
                    AddContactBottomSheetFragment bottomSheet = new AddContactBottomSheetFragment();
                    bottomSheet.show(activity.getSupportFragmentManager(), "AddContactBottomSheet");
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return personalContacts.size() + 1 ; // +1 to add the last view holder for add a new contact
    }

    @Override
    public int getItemViewType(int position) {
        if (position < personalContacts.size()) {
            return TYPE_CONTACT;
        } else {
            return TYPE_ADD_BUTTON;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactNameTextView;
        public TextView contactNumberTextView;
        public ImageView contactIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.contactNameTextView);
            contactNumberTextView = itemView.findViewById(R.id.contactNumberTextView);
            contactIconImageView = itemView.findViewById(R.id.contactIconImageView);
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        public TextView addContactTextView;
        public ImageView addIconImageView;

        public AddButtonViewHolder(View itemView) {
            super(itemView);
            addContactTextView = itemView.findViewById(R.id.addContactTextView);
            addIconImageView = itemView.findViewById(R.id.addIconImageView);
        }
    }
}
