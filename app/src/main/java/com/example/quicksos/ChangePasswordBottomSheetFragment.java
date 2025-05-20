package com.example.quicksos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordBottomSheetFragment extends BottomSheetDialogFragment {

    // Interface for communication with parent fragment/activity
    public interface OnPasswordChangedListener {
        void onPasswordChanged();
    }

    private OnPasswordChangedListener listener;
    private TextInputEditText currentPasswordEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText newPasswordConfirmationEditText;
    private TextInputLayout currentPasswordInputLayout;
    private TextInputLayout newPasswordInputLayout;
    private TextInputLayout newPasswordConfirmationInputLayout;
    private Button saveButton;
    private Button cancelButton;

    @Override
    public void onStart() {
        super.onStart();

        // Expand the Bottom Sheet completely when open it
        if (getDialog() != null) {
            View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                behavior.setSkipCollapsed(true);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Try to get the listener from the parent fragment
            if (getParentFragment() instanceof OnPasswordChangedListener) {
                listener = (OnPasswordChangedListener) getParentFragment();
            } else if (context instanceof OnPasswordChangedListener) {
                listener = (OnPasswordChangedListener) context;
            }
        } catch (ClassCastException e) {}
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment_change_password, container, false);

        // Initialize views
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        newPasswordConfirmationEditText = view.findViewById(R.id.newPasswordConfirmationEditText);
        currentPasswordInputLayout = view.findViewById(R.id.currentPasswordInputLayout);
        newPasswordInputLayout = view.findViewById(R.id.newPasswordInputLayout);
        newPasswordConfirmationInputLayout = view.findViewById(R.id.newPasswordConfirmationInputLayout);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        // Setup button listeners
        saveButton.setOnClickListener(v -> validateAndChangePassword());
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void validateAndChangePassword() {
        // Clear previous errors
        currentPasswordInputLayout.setError(null);
        newPasswordInputLayout.setError(null);
        newPasswordConfirmationInputLayout.setError(null);

        // Get input values
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = newPasswordConfirmationEditText.getText().toString().trim();

        // Validate inputs
        if (currentPassword.isEmpty()) {
            currentPasswordInputLayout.setError(getString(R.string.current_password_empty));
            return;
        }

        if (newPassword.isEmpty()) {
            newPasswordInputLayout.setError(getString(R.string.password_empty));
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordInputLayout.setError(getString(R.string.short_password));
            return;
        }

        if (confirmPassword.isEmpty()) {
            newPasswordConfirmationInputLayout.setError(getString(R.string.repeat_password_empty));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            newPasswordConfirmationInputLayout.setError(getString(R.string.unmatched_password));
            return;
        }

        // All validation passed, proceed with reauthentication and password change
        reauthenticateAndChangePassword(currentPassword, newPassword);
    }

    private void reauthenticateAndChangePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Create credential with current email and password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Reauthenticate
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Reauthentication successful, now change password
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), R.string.successfully_password_changed, Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.onPasswordChanged();
                                    }
                                    dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), getString(R.string.failed_password_changed) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Incorrect current password or other authentication issue
                        currentPasswordInputLayout.setError(getString(R.string.incorrect_password));
                    });
        } else {
            Toast.makeText(getContext(), R.string.user_not_found, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}