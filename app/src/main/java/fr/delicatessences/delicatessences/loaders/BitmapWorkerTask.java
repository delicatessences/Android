package fr.delicatessences.delicatessences.loaders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Drawable> {
    private final WeakReference<ImageView> imageViewReference;
    private final Context mContext;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        mContext = context;
        imageViewReference = new WeakReference<>(imageView);
    }


    @Override
    protected Drawable doInBackground(Integer... params) {
        int data = params[0];
        return ContextCompat.getDrawable(mContext, data);
    }


    @Override
    protected void onPostExecute(Drawable drawable) {
        ImageView imageView = imageViewReference.get();
        if (imageView != null && drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }
}