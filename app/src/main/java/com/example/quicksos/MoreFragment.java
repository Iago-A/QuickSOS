package com.example.quicksos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment implements ChangePasswordBottomSheetFragment.OnPasswordChangedListener {
    private boolean isGuestMode = false;

    public MoreFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Check if we're in guest mode from arguments
        if (getArguments() != null) {
            isGuestMode = getArguments().getBoolean("isGuestMode", false);
        }

        View view;

        if (isGuestMode) {
            view = inflater.inflate(R.layout.fragment_more_guest, container, false);
            setupGuestMode(view);
        } else {
            view = inflater.inflate(R.layout.fragment_more, container, false);
            setupAuthenticatedMode(view);
        }

        // Configure app version for both modes
        setupAppVersion(view);

        return view;
    }

    private void setupGuestMode(View view) {
        view.findViewById(R.id.exitGuestButton).setOnClickListener(v -> {
            // Clear guest mode and return to login
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("QuickSOSPrefs", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isGuestMode", false);
            editor.putBoolean("isAuthenticated", false);
            editor.apply();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void setupAuthenticatedMode(View view) {
        // Listeners configuration for authenticated users
        view.findViewById(R.id.changePasswordLayout).setOnClickListener(v -> {
            ChangePasswordBottomSheetFragment bottomSheet = new ChangePasswordBottomSheetFragment();
            bottomSheet.show(getChildFragmentManager(), "ChangePasswordBottomSheet");
        });

        view.findViewById(R.id.logoutLayout).setOnClickListener(v -> {
            // Clear SharedPreferences when logging out
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("QuickSOSPrefs", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isGuestMode", false);
            editor.putBoolean("isAuthenticated", false);
            editor.apply();

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        view.findViewById(R.id.deleteAccountLayout).setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });
    }

    private void setupAppVersion(View view) {
        TextView versionTextView = view.findViewById(R.id.versionTextView);
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionTextView.setText(getString(R.string.app_version_format, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            versionTextView.setVisibility(View.GONE);
        }
    }

    private void showDeleteAccountConfirmationDialog() {
        // Create an EditText for the confirmation delete text
        final EditText input = new EditText(requireContext());
        input.setHint(R.string.delete_account_type_confirmation);

        // Configurate EditText style
        input.setBackgroundResource(R.drawable.edit_text_background);
        input.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        input.setHintTextColor(getResources().getColor(R.color.color_hint, null));

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
        AlertDialog dialog = builder.create();
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

    @Override
    public void onPasswordChanged() {
        // We don't need to do anything specific here
    }
}