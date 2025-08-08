package com.hdvideoplayer.smartplayer.player.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.hdvideoplayer.smartplayer.player.R;

//public class SortDialogBuilder {
//    private final Dialog mDialog;
//    private RadioButton rbAscending;
//    private RadioButton rbDate;
//    private RadioButton rbDescending;
//    private RadioButton rbLength;
//    private RadioButton rbName;
//    private RadioButton rbSize;
//    private RadioGroup rgSort;
//    private RadioGroup rgUnit;
//
//    public interface OkButtonClickListener {
//        void onClick(int i, boolean z);
//    }
//    public SortDialogBuilder(Context context, final OkButtonClickListener okButtonClickListener, int i, boolean z) {
//        Dialog dialog = new Dialog(context, R.style.CustomDialog);
//        this.mDialog = dialog;
//        dialog.requestWindowFeature(1);
//        dialog.setContentView(R.layout.dialog_sort);
//
//        Window window = this.mDialog.getWindow();
//        if (window != null) {
//            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setGravity(Gravity.CENTER);
//        }
//        this.rbName = (RadioButton) dialog.findViewById(R.id.rb_name);
//        this.rbSize = (RadioButton) dialog.findViewById(R.id.rb_size);
//        this.rbDate = (RadioButton) dialog.findViewById(R.id.rb_date);
//        this.rbLength = (RadioButton) dialog.findViewById(R.id.rb_length);
//        this.rgUnit = (RadioGroup) dialog.findViewById(R.id.rg_unit);
//        this.rgSort = (RadioGroup) dialog.findViewById(R.id.rg_sort);
//        this.rbAscending = (RadioButton) dialog.findViewById(R.id.rb_ascending);
//        this.rbDescending = (RadioButton) dialog.findViewById(R.id.rb_descending);
//        if (i == 0) {
//            this.rbDate.setChecked(true);
//        } else if (i == 1) {
//            this.rbName.setChecked(true);
//        } else if (i == 2) {
//            this.rbSize.setChecked(true);
//        } else if (i == 3) {
//            this.rbLength.setChecked(true);
//        }
//        if (z) {
//            this.rbAscending.setChecked(true);
//        } else {
//            this.rbDescending.setChecked(true);
//        }
//        dialog.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new OnClickListener() {
//            public final void onClick(View view) {
//                mDialog.dismiss();
//            }
//        });
//        dialog.findViewById(R.id.tv_dialog_ok).setOnClickListener(new OnClickListener() {
//            public final void onClick(View view) {
//                mDialog.dismiss();
//                boolean z = false;
//                int i = rgUnit.getCheckedRadioButtonId() == R.id.rb_length ? 3 : rgUnit.getCheckedRadioButtonId() == R.id.rb_name ? 1 : rgUnit.getCheckedRadioButtonId() == R.id.rb_size ? 2 : 0;
//                if (rgSort.getCheckedRadioButtonId() == R.id.rb_ascending) {
//                    z = true;
//                }
//                okButtonClickListener.onClick(i, z);
//
//            }
//        });
//    }
//
//    public Dialog build() {
//        return this.mDialog;
//    }
//}

public class SortDialogBuilder {
    private final Dialog mDialog;

    private RadioButton rbAscending,rbDate,rbDescending,rbLength,rbName,rbSize;

    private RadioGroup rgSortOptions,rgSortOrder;


    // Interface to handle the OK button click event
    public interface OkButtonClickListener {
        void onClick(int sortOption, boolean isAscending);
    }

    public SortDialogBuilder(Context context, final OkButtonClickListener okButtonClickListener, int selectedOption, boolean isAscending) {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        this.mDialog = dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sort);

        // Set dialog window properties
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        // Initialize UI components
        this.rbName = dialog.findViewById(R.id.rb_name);
        this.rbSize = dialog.findViewById(R.id.rb_size);
        this.rbDate = dialog.findViewById(R.id.rb_date);
        this.rbLength = dialog.findViewById(R.id.rb_length);
        this.rgSortOptions = dialog.findViewById(R.id.rg_unit);
        this.rgSortOrder = dialog.findViewById(R.id.rg_sort);
        this.rbAscending = dialog.findViewById(R.id.rb_ascending);
        this.rbDescending = dialog.findViewById(R.id.rb_descending);

        // Set the initial selection for sort options
        if (selectedOption == 0) {
            this.rbDate.setChecked(true);
        } else if (selectedOption == 1) {
            this.rbName.setChecked(true);
        } else if (selectedOption == 2) {
            this.rbSize.setChecked(true);
        } else if (selectedOption == 3) {
            this.rbLength.setChecked(true);
        }

        // Set the initial selection for sort order
        if (isAscending) {
            this.rbAscending.setChecked(true);
        } else {
            this.rbDescending.setChecked(true);
        }

        // Set up the Cancel button
        dialog.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        // Set up the OK button
        dialog.findViewById(R.id.tv_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();

                // Determine selected sort option
                int selectedSortOption;
                if (rgSortOptions.getCheckedRadioButtonId() == R.id.rb_name) {
                    selectedSortOption = 1;
                } else if (rgSortOptions.getCheckedRadioButtonId() == R.id.rb_size) {
                    selectedSortOption = 2;
                } else if (rgSortOptions.getCheckedRadioButtonId() == R.id.rb_length) {
                    selectedSortOption = 3;
                } else {
                    selectedSortOption = 0; // Default to date
                }

                // Determine selected sort order
                boolean isAscending = rgSortOrder.getCheckedRadioButtonId() == R.id.rb_ascending;

                // Trigger the callback
                okButtonClickListener.onClick(selectedSortOption, isAscending);
            }
        });
    }

    // Build and return the dialog
    public Dialog build() {
        return this.mDialog;
    }
}

