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

public class PersonalContactsAdapter extends RecyclerView.Adapter<PersonalContactsAdapter.ViewHolder> {
    private ArrayList<PersonalContact> personalContacts;

    public PersonalContactsAdapter(ArrayList<PersonalContact> personalContacts) {
        this.personalContacts = personalContacts;
    }

    @NonNull
    @Override
    public PersonalContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalContactsAdapter.ViewHolder holder, int position) {
        PersonalContact personalContact = personalContacts.get(position);

        holder.nameTextView.setText(personalContact.getName());
        holder.numberTextView.setText(personalContact.getNumber());

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(personalContact.getUrl())
                .apply(requestOptions)
                .into(holder.iconImageView);

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
    }

    @Override
    public int getItemCount() {
        return personalContacts.size();
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
