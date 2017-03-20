package fr.delicatessences.delicatessences.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class CustomAsyncTaskLoader<D> extends AsyncTaskLoader<D> {


    private final Context mActivity;
    private D mData;

    public CustomAsyncTaskLoader(Context context) {
        super(context);
        mActivity = context;
    }


    protected Context getActivity(){
        return mActivity;
    }

    @Override
    public void deliverResult(D result) {
        if (isReset()) {
            return;
        }

        mData = result;

        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mData != null) {
            mData = null;
        }
    }
}