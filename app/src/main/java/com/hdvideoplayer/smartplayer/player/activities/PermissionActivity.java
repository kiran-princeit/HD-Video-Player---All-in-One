package com.hdvideoplayer.smartplayer.player.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.System;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.Const;
import com.hdvideoplayer.smartplayer.player.Pref;
import com.hdvideoplayer.smartplayer.player.util.PermissionUtils;


public class PermissionActivity extends BaseActivity {
    String[] PERMISSTIONSAbove = new String[]{
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.POST_NOTIFICATIONS"
    };
    String[] PERMISSTIONSBelow = new String[]{
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    private Button btnPermission;
    private Button btnNext;
    private int isactivityopen = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_permission); // Set your layout here

        // Initialize views
        btnPermission = findViewById(R.id.btnPermission);
        btnNext = findViewById(R.id.btnNext);

        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        btnNext.startAnimation(animShake);


        final Animation animShake2 = AnimationUtils.loadAnimation(this, R.anim.shake);
        btnPermission.startAnimation(animShake2);


        // Button click listeners
        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionUtils.checkStoragePermission(PermissionActivity.this)) {
                    requestStoragePermission();
                } else if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (System.canWrite(PermissionActivity.this)) {
                        Pref.getInstance().setString(Const.NEXT, "permission");
                        Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        openAndroidPermissionsMenu();
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageView btnPermission1 = findViewById(R.id.btnPermission1);
        ImageView btnPermission2 = findViewById(R.id.btnPermission2);

        if (PermissionUtils.checkStoragePermission(this)) {
            btnPermission1.setVisibility(View.VISIBLE);
        } else {
            btnPermission1.setVisibility(View.GONE);
        }

        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (System.canWrite(this)) {
                btnPermission2.setVisibility(View.VISIBLE);
            } else {
                btnPermission2.setVisibility(View.GONE);
            }
        }

        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.checkStoragePermission(this) && System.canWrite(this)) {
                btnNext.setVisibility(View.VISIBLE);
                btnPermission.setVisibility(View.GONE);
            } else {
                btnNext.setVisibility(View.GONE);
                btnPermission.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to open settings. Please grant permissions manually.", Toast.LENGTH_LONG).show();
        }
    }


//    private void openAndroidPermissionsMenu() {
//        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
//        intent.setData(Uri.parse("package:" + getPackageName()));
//        startActivity(intent);
//    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            Dexter.withContext(this).withPermissions(PERMISSTIONSAbove).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (!report.areAllPermissionsGranted() || report.isAnyPermissionPermanentlyDenied()) {
                        showSimpleDialog();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        } else {
            Dexter.withContext(this).withPermissions(PERMISSTIONSBelow).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (!report.areAllPermissionsGranted() || report.isAnyPermissionPermanentlyDenied()) {
                        showSimpleDialog();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        }
    }

    private void showSimpleDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_permission);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        AppCompatButton btnAllow = dialog.findViewById(R.id.btnAllow);
        TextView btnCancel = dialog.findViewById(R.id.btnCancle);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                isactivityopen = 1;
                startActivity(intent);
            }
        });

        dialog.show();
    }
}

