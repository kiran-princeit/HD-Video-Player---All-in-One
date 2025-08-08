package com.hdvideoplayer.smartplayer.player.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.hdvideoplayer.smartplayer.player.R;

public class QuestionDialogBuilder {
    private final Dialog mDialog;

    public interface OkButtonClickListener {
        void onCancelClick();

        void onOkClick();
    }

    public QuestionDialogBuilder(Context context, final OkButtonClickListener okButtonClickListener) {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        this.mDialog = dialog;
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_question);

        Window window = this.mDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
        dialog.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                okButtonClickListener.onCancelClick();
                mDialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_dialog_ok).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                okButtonClickListener.onOkClick();
                mDialog.dismiss();
            }
        });
    }

    public QuestionDialogBuilder setTitle(int i, int i2) {
        TextView textView = (TextView) this.mDialog.findViewById(R.id.tv_dialog_title);
//        textView.setTextColor(i2);
        textView.setText(i);
        return this;
    }

    public QuestionDialogBuilder setQuestion(int i) {
        ((TextView) this.mDialog.findViewById(R.id.tv_question)).setText(i);
        return this;
    }

    public QuestionDialogBuilder setQuestion(String str) {
        ((TextView) this.mDialog.findViewById(R.id.tv_question)).setText(str);
        return this;
    }
    public Dialog build() {
        return this.mDialog;
    }
}
