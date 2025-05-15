package com.example.quicksos;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    // Código para eliminar la cuenta
                    FirebaseAuth.getInstance().getCurrentUser().delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), R.string.successfully_account_deleted, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), R.string.failed_account_deleted, Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
