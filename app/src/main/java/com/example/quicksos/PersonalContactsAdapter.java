package com.example.quicksos;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

            contactHolder.nameTextView.setText(personalContact.getName());
            contactHolder.numberTextView.setText(personalContact.getNumber());

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(holder.itemView.getContext())
                    .load(personalContact.getUrl())
                    .apply(requestOptions)
                    .into(contactHolder.iconImageView);

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
//                Intent intent = new Intent(v.getContext(), AddContactActivity.class);
//                v.getContext().startActivity(intent);
                    Toast.makeText(v.getContext(), "Añadir contacto", Toast.LENGTH_SHORT).show();
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
        public TextView nameTextView;
        public TextView numberTextView;
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name_text);
            numberTextView = itemView.findViewById(R.id.contact_number_text);
            iconImageView = itemView.findViewById(R.id.contact_icon);
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        public AddButtonViewHolder(View itemView) {
            super(itemView);
            // Reference views??
        }
    }
}
