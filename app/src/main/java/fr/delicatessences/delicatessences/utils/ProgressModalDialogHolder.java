package fr.delicatessences.delicatessences.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Helper class to manage a ProgressDialog.
 */
public class ProgressModalDialogHolder {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public ProgressModalDialogHolder(Context context) {
        mContext = context;
    }

    private void showLoadingDialog(String message) {
        dismissDialog();

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle("");
        }

        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void showLoadingDialog(@StringRes int stringResource) {
        showLoadingDialog(mContext.getString(stringResource));
    }

    public void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

}
