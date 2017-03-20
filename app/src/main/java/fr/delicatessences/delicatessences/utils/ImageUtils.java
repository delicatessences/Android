package fr.delicatessences.delicatessences.utils;

import android.content.Context;
import android.widget.ImageView;

import fr.delicatessences.delicatessences.loaders.BitmapWorkerTask;

public class ImageUtils {


    public static void loadDrawable(Context context, int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(context, imageView);
        task.execute(resId);
    }

}
