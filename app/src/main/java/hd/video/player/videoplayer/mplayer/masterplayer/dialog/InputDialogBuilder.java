package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import hd.video.player.videoplayer.mplayer.masterplayer.R;

public class InputDialogBuilder {
    private final Dialog mDialog;

    public interface OkButtonClickListener {
        void onClick(String str);
    }

    public InputDialogBuilder(Context context, final OkButtonClickListener okButtonClickListener, String str) {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        this.mDialog = dialog;
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_input_text);
        dialog.getWindow().setSoftInputMode(4);
        final EditText editText = (EditText) dialog.findViewById(R.id.edt_input_text);
        if (!TextUtils.isEmpty(str)) {
            editText.setText(str);
        }
        dialog.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                InputDialogBuilder.this.m611x15bae3a3(view);
            }
        });
        dialog.findViewById(R.id.tv_dialog_ok).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                okButtonClickListener.onClick(editText.getText().toString());
                InputDialogBuilder.this.mDialog.dismiss();
            }
        });
    }

    public void m611x15bae3a3(View view) {
        this.mDialog.dismiss();
    }

    public InputDialogBuilder setTitle(int i, int i2) {
        TextView textView = (TextView) this.mDialog.findViewById(R.id.tv_dialog_title);
        textView.setTextColor(i2);
        textView.setText(i);
        return this;
    }

    public Dialog build() {
        return this.mDialog;
    }
}
