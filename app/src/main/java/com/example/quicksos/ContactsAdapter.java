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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Locale;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static final String TAG = "ContactsAdapter";
    private ArrayList<Contact> contacts;

    public ContactsAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        // Obtain the phone's language
        String language = Locale.getDefault().getLanguage();
        String contactName = "";

        Contact contact = contacts.get(position);

        if (language.equals("es")) {
            contactName = contact.getNameEs();
        } else {
            contactName = contact.getNameEn();
        }
        holder.nameTextView.setText(contactName);
        holder.numberTextView.setText(contact.getNumber());

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(contact.getUrl())
                .apply(requestOptions)
                .into(holder.iconImageView);

        // Listener for write contact number directly in the phone
        String finalContactName = contactName;
        holder.itemView.setOnClickListener(v -> {
            String phoneNumber = contact.getNumber();

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
    }

    @Override
    public int getItemCount() {
        return contacts.size();
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
}

