package fr.delicatessences.delicatessences.fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollView;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
import fr.delicatessences.delicatessences.interfaces.Reloadable;
import fr.delicatessences.delicatessences.utils.ImageUtils;

public abstract class DetailFragment extends Fragment
        implements OnScrollChangedCallback, LoaderManager.LoaderCallbacks<Object>, Reloadable{

    private static final String DETAIL_DISPLAY_COUNT_PREF = "detailDisplayCount";
    private final static int DETAIL_DISPLAY_COUNT = 2;

    private Toolbar mToolbar;
    private ColorDrawable mActionBarBackgroundDrawable;
    private ImageView mHeader;
    private TextView mTitle;
    private int mLastDampedScroll;
    private int mLastScrollPosition;
    private int mInitialStatusBarColor;
    private int mFinalStatusBarColor;
    private SystemBarTintManager mStatusBarManager;
    private ViewGroup mRubber;
    private int mId;
    private ObservableScrollView mScrollView;
    private DrawerLayout mDrawerLayout;
    protected String mIndexedName;
    protected String mIndexedText;
    protected String mIndexedURL;
    private GoogleApiClient mClient;
    private ShowcaseView mShowcaseView;

    protected abstract int getLayout();


    int getmId() {
        return mId;
    }

    TextView getTitle() {
        return mTitle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(AppIndex.API).build();
        View view = inflater.inflate(getLayout(), container, false);

        FragmentActivity activity = getActivity();
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        Resources resources = getResources();
        boolean land = resources.getBoolean(R.bool.land);

        mToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        mActionBarBackgroundDrawable = new ColorDrawable();
        mToolbar.setBackground(mActionBarBackgroundDrawable);
        mStatusBarManager = new SystemBarTintManager(activity);
        mStatusBarManager.setStatusBarTintEnabled(true);
        mInitialStatusBarColor = Color.BLACK;
        mHeader = (ImageView) view.findViewById(R.id.header);
        mTitle = (TextView) view.findViewById(R.id.title);
        mRubber = (ViewGroup) view.findViewById(R.id.rubber);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollview);

        if (!land){
            mScrollView.setOnScrollChangedCallback(this);
        }

        return view;
    }


    protected abstract Action getAction();


    @Override
    public void onStop() {
        try{
            Action viewAction = getAction();
            AppIndex.AppIndexApi.end(mClient, viewAction);
            mClient.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        ViewGroup.LayoutParams lp = mToolbar.getLayoutParams();
        lp.width = width;
        mToolbar.setLayoutParams(lp);

        Resources resources = getResources();
        mActionBarBackgroundDrawable.setColor(resources.getColor(R.color.primary_color));
        mStatusBarManager.setTintColor(resources.getColor(R.color.primary_color));
        super.onStop();
    }



    @Override
    public void onScroll(int l, int scrollPosition) {

        int headerHeight = mHeader.getHeight() - mToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        updateActionBarTransparency(ratio);
        updateStatusBarColor(ratio);
        updateParallaxEffect(ratio, scrollPosition);
        mLastScrollPosition = scrollPosition;
    }



    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mActionBarBackgroundDrawable.setAlpha(newAlpha);
        mToolbar.setBackground(mActionBarBackgroundDrawable);
        mTitle.setAlpha((255F - newAlpha) / 255F);
    }




    private void updateStatusBarColor(float scrollRatio) {
        int r = interpolate(Color.red(mInitialStatusBarColor), Color.red(mFinalStatusBarColor), 1 - scrollRatio);
        int g = interpolate(Color.green(mInitialStatusBarColor), Color.green(mFinalStatusBarColor), 1 - scrollRatio);
        int b = interpolate(Color.blue(mInitialStatusBarColor), Color.blue(mFinalStatusBarColor), 1 - scrollRatio);
        mStatusBarManager.setTintColor(Color.rgb(r, g, b));
    }



    private void updateParallaxEffect(float ratio, int scrollPosition) {
        if (ratio == 1 && mLastDampedScroll == 0){
            return;
        }
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeader.offsetTopAndBottom(-offset);
        mLastDampedScroll = dampedScroll;
    }



    private int interpolate(int from, int to, float param) {
        return (int) (from * param + to * (1 - param));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);


        Resources resources = getResources();
        Target target = new Target(){

            @Override
            public Point getPoint() {
                View view = mToolbar.findViewById(R.id.action_edit);
                if (view != null){
                    return new ViewTarget(view).getPoint();
                }

                return null;
            }
        };

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        int displayCount = preferences.getInt(DETAIL_DISPLAY_COUNT_PREF, 1);

        if (displayCount == DETAIL_DISPLAY_COUNT) {
            mShowcaseView = new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setTarget(target)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setContentTitle(resources.getString(R.string.detail_showcase_title))
                    .setContentText(resources.getString(R.string.detail_showcase_content))
                    .replaceEndButton(R.layout.view_custom_button)
                    .build();
            mShowcaseView.setButtonPosition(lps);
        }

        SharedPreferences.Editor e = preferences.edit();
        e.putInt(DETAIL_DISPLAY_COUNT_PREF, displayCount + 1);
        e.apply();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:
                if (mShowcaseView != null && mShowcaseView.isShown()){
                    mShowcaseView.hide();
                }
                updateActionBarTransparency(0);
                updateStatusBarColor(0);
                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        switch (loader.getId()){
            case 0:
                if (o instanceof SheetAdapter){
                    SheetAdapter sheetAdapter = (SheetAdapter) o;
                    Resources resources = getResources();
                    FragmentActivity activity = getActivity();
                    if (mHeader.getDrawable() == null) {
                        int resLowId = resources.getIdentifier(sheetAdapter.getImageLowRes(), "drawable", activity.getPackageName());
                        mHeader.setImageDrawable(ContextCompat.getDrawable(activity, resLowId));
                        int resId = resources.getIdentifier(sheetAdapter.getImage(), "drawable", activity.getPackageName());
                        ImageUtils.loadDrawable(activity, resId, mHeader);
                    }
                    int color = sheetAdapter.getColor();

                    mActionBarBackgroundDrawable.setColor(color);
                    mFinalStatusBarColor = color;
                    mRubber.setBackgroundColor(color);

                    boolean land = resources.getBoolean(R.bool.land);
                    if (land){
                        updateActionBarTransparency(0);
                        updateStatusBarColor(0);
                    }else{
                        onScroll(-1, mLastScrollPosition);
                    }


                    updateIndex();
                    try {
                        mClient.connect();
                        Action viewAction = getAction();
                        AppIndex.AppIndexApi.start(mClient, viewAction);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;


            default: break;
        }


    }



    private void updateIndex() {
        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(mIndexedName != null ? mIndexedName : "")
                .setText(mIndexedText != null ? mIndexedText : "")
                .setUrl(mIndexedURL)
                .build();

        FirebaseAppIndex.getInstance().update(indexable);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MainActivity activity = (MainActivity) getActivity();
        activity.setDrawerIndicatorEnabled(false);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mId = getArguments().getInt(MainActivity.EXTRA_ID, 0);

        if (!(activity instanceof OrmLiteBaseActionBarActivity)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OrmLiteBaseActionBarActivity");
        }


        setHasOptionsMenu(true);

    }


    void showConfirmDialog(final MainActivity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok), null);
        builder.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        reload();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mLastScrollPosition = 0;
        mScrollView.scrollTo(0,0);
        Resources resources = getResources();
        boolean land = resources.getBoolean(R.bool.land);
        if (land){
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            TypedValue imageRatio = new TypedValue();
            resources.getValue(R.dimen.image_ratio, imageRatio, true);
            int width = (int) (size.x * imageRatio.getFloat());
            ViewGroup.LayoutParams lp = mToolbar.getLayoutParams();
            lp.width = width;
            mToolbar.setLayoutParams(lp);
            updateActionBarTransparency(0);
        }


    }


    @Override
    public void reload() {
        getLoaderManager().restartLoader(0, null, this);
    }


}
