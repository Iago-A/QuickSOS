package com.example.quicksos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment {

    public MoreFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        // Listeners configuration
        view.findViewById(R.id.changePasswordLayout).setOnClickListener(v -> {
            // Implements code to change password
        });

        view.findViewById(R.id.logoutLayout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        view.findViewById(R.id.deleteAccountLayout).setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });

        // Configurate app version
        TextView versionTextView = view.findViewById(R.id.versionTextView);
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionTextView.setText(getString(R.string.app_version_format, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            versionTextView.setVisibility(View.GONE);
        }

        return view;
    }

    private void showDeleteAccountConfirmationDialog() {
        // Create an EditText for the confirmation delete text
        final EditText input = new EditText(requireContext());
        input.setHint(R.string.delete_account_type_confirmation);

        // Configurate EditText style
        input.setBackgroundResource(R.drawable.edit_text_background);
        input.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12)
        );
        input.setHintTextColor(getResources().getColor(R.color.colorHint, null));

        // Create the layout to contain the EditText
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(16);
        container.setPadding(padding, padding, padding, padding);
        container.addView(input);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_confirmation)
                .setView(container)
                .setPositiveButton(R.string.delete, null)
                .setNegativeButton(R.string.cancel, null);

        // Create and show dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Overwrite the positive button to validate the entry before proceeding
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String confirmationPhrase = getString(R.string.delete_account_confirmation_phrase);
            String userInput = input.getText().toString().trim();

            if (userInput.equalsIgnoreCase(confirmationPhrase)) {
                FirebaseAuth.getInstance().getCurrentUser().delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), R.string.successfully_account_deleted, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), R.string.failed_account_deleted, Toast.LENGTH_SHORT).show();
                        });
                dialog.dismiss();
            } else {
                input.setError(getString(R.string.delete_account_confirmation_error));
            }
        });
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }
}
