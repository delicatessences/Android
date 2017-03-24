package fr.delicatessences.delicatessences.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.EditBottleActivity;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.adapters.BottleSheetAdapter;
import fr.delicatessences.delicatessences.adapters.SheetAdapter;
import fr.delicatessences.delicatessences.loaders.CustomAsyncTaskLoader;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EssentialOil;

public class DetailBottleFragment extends DetailFragment {


    private ImageView mOilAvatar;
    private TextView mPrice;
    private TextView mExpirationDate;
    private TextView mCapacity;
    private TextView mBrand;
    private TextView mBrandLabel;
    private TextView mOrigin;
    private TextView mOriginLabel;
    private ImageView mPure;
    private ImageView mBio;
    private ImageView mHebbd;
    private ImageView mHect;
    private TextView mQualityLabel;

    @Override
    protected int getLayout() {
        return R.layout.fragment_detail_bottle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        assert view != null;
        mOilAvatar = (ImageView) view.findViewById(R.id.avatar);
        mPrice = (TextView) view.findViewById(R.id.price_label);
        mExpirationDate = (TextView) view.findViewById(R.id.expiration_label);
        mCapacity = (TextView) view.findViewById(R.id.capacity_label);
        mBrand = (TextView) view.findViewById(R.id.brand);
        mBrandLabel = (TextView) view.findViewById(R.id.brand_label);
        mOrigin = (TextView) view.findViewById(R.id.origin);
        mOriginLabel = (TextView) view.findViewById(R.id.origin_label);
        mPure = (ImageView) view.findViewById(R.id.pure_image);
        mBio = (ImageView) view.findViewById(R.id.bio_image);
        mHebbd = (ImageView) view.findViewById(R.id.hebbd_image);
        mHect = (ImageView) view.findViewById(R.id.hect_image);
        mQualityLabel = (TextView) view.findViewById(R.id.quality_label);

        return view;
    }


    public static DetailBottleFragment newInstance(int id) {
        DetailBottleFragment fragment = new DetailBottleFragment();

        Bundle args = new Bundle();
        args.putInt(MainActivity.EXTRA_ID, id);
        fragment.setArguments(args);

        return fragment;
    }


    //up button action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivity activity = (MainActivity) getActivity();
        int mId = getmId();

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.action_edit:
                Intent intent = new Intent(activity, EditBottleActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, mId);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                showDialog(activity, getResources().getString(R.string.delete_bottle_warning));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_detail_bottle_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }








    private void showDialog(final MainActivity activity, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        final Resources resources = getResources();
        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteBottle();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_bottle));
                            activity.showList(ViewType.BOTTLES);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            activity.showFeedbackMessage(resources.getString(R.string.delete_failed));
                        }
                    }
                }
        );
        builder.setNegativeButton(resources.getString(R.string.action_cancel), null);
        builder.show();
    }


    private void deleteBottle() throws SQLException {
        MainActivity activity = (MainActivity) getActivity();
        final DatabaseHelper helper = activity.getHelper();
        int mId = getmId();

        Dao<Bottle, Integer> dao = helper.getBottleDao();
        DeleteBuilder<Bottle, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.where().eq(Bottle.ID_FIELD_NAME, mId);
        deleteBuilder.delete();

        FirebaseAppIndex.getInstance().remove(mIndexedURL);

    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();
        return new CustomAsyncTaskLoader<Object>(activity){

            @Override
            public SheetAdapter loadInBackground() {
                DatabaseHelper helper = (DatabaseHelper) activity.getHelper();
                SheetAdapter adapter = null;
                try {
                    Dao<Bottle, Integer> bottleDao = helper.getBottleDao();
                    int mId = getmId();
                    Bottle bottle = bottleDao.queryForId(mId);
                    if (bottle != null) {
                        Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
                        EssentialOil essentialOil = essentialOilDao.queryForId(bottle.getEssentialOil().getId());
                        if (essentialOil != null) {
                            prepareIndex(bottle, essentialOil);
                            adapter = new BottleSheetAdapter(bottle.getBrand(), bottle.getPrice(), bottle.getCapacity(),
                                    bottle.getExpiration(), bottle.getOrigin(), bottle.isBio(), bottle.isPure(),
                                    bottle.isHect(), bottle.isHebbd(), bottle.getImage(), Color.parseColor(bottle.getColor()),
                                    essentialOil.getName(), essentialOil.getImage());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return adapter;
            }
        };
    }


    private void prepareIndex(Bottle bottle, EssentialOil essentialOil){
        mIndexedURL = bottle.getUrl();
        String brand = bottle.getBrand();
        Resources resources = getResources();
        String withoutBrand = resources.getString(R.string.without_brand);
        String namePrefix = resources.getString(R.string.bottle_of);
        String nameSuffix = " " + ((brand != null && brand.length() > 0) ? "(" + brand + ")" : withoutBrand);
        mIndexedName = namePrefix + essentialOil.getName() + nameSuffix;
        mIndexedText = essentialOil.getDescription();
    }

    @Override
    protected Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mIndexedName)
                .setUrl(Uri.parse(mIndexedURL))
                .build();


        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .build();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }


    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        super.onLoadFinished(loader, o);
        final OrmLiteBaseActionBarActivity activity = (OrmLiteBaseActionBarActivity) getActivity();
        if (o instanceof BottleSheetAdapter){
            BottleSheetAdapter adapter = (BottleSheetAdapter) o;
            Resources resources = getResources();
            String name = adapter.getOilName();
            TextView mTitle = getTitle();
            mTitle.setText(name != null && name.length() > 0 ? name : resources.getString(R.string.without_name));
            mTitle.setVisibility(View.VISIBLE);
            int avatarId = resources.getIdentifier(adapter.getOilImage(), "drawable", activity.getPackageName());
            mOilAvatar.setImageDrawable(ContextCompat.getDrawable(activity, avatarId));
            mOilAvatar.setVisibility(View.VISIBLE);

            Double price = adapter.getPrice();
            if (price != null){
                StringBuilder sb = new StringBuilder();
                Locale currentLocale = getResources().getConfiguration().locale;
                sb.append(String.format(currentLocale, "%.3g", price));
                sb.append(" €");
                mPrice.setText(sb.toString());
                mPrice.setVisibility(View.VISIBLE);
            }
            else{
                mPrice.setVisibility(View.GONE);
            }

            Date expiration = adapter.getExpiration();
            if (expiration != null){
                Calendar cal = Calendar.getInstance();
                cal.setTime(expiration);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                String monthS = month < 10  ? String.format("%02d", month) : String.valueOf(month);
                mExpirationDate.setText(monthS + "/" + year);
                mExpirationDate.setVisibility(View.VISIBLE);
            }
            else{
                mExpirationDate.setVisibility(View.GONE);
            }

            Integer capacity = adapter.getCapacity();
            if (capacity != null){
                mCapacity.setText(capacity.toString() + " ml");
                mCapacity.setVisibility(View.VISIBLE);
            }else{
                mCapacity.setVisibility(View.GONE);
            }


            String brand = adapter.getBrand();
            if (brand != null && brand.length() > 0){
                mBrand.setText(brand);
                mBrand.setVisibility(View.VISIBLE);
                mBrandLabel.setTextColor(adapter.getColor());
                mBrandLabel.setVisibility(View.VISIBLE);
            }else{
                mBrand.setVisibility(View.GONE);
                mBrandLabel.setVisibility(View.GONE);
            }

            String origin = adapter.getOrigin();
            if (origin != null && origin.length() > 0){
                mOrigin.setText(origin);
                mOrigin.setVisibility(View.VISIBLE);
                mOriginLabel.setTextColor(adapter.getColor());
                mOriginLabel.setVisibility(View.VISIBLE);
            }else{
                mOrigin.setVisibility(View.GONE);
                mOriginLabel.setVisibility(View.GONE);
            }

            boolean showQualityLabel = false;

            if (adapter.isPure()){
                showQualityLabel = true;
                mPure.setVisibility(View.VISIBLE);
                GradientDrawable background = (GradientDrawable) mPure.getBackground();
                background.setColor(adapter.getColor());
            }else{
                mPure.setVisibility(View.GONE);
            }

            if (adapter.isBio()){
                showQualityLabel = true;
                mBio.setVisibility(View.VISIBLE);
                GradientDrawable background = (GradientDrawable) mBio.getBackground();
                background.setColor(adapter.getColor());
            }else{
                mBio.setVisibility(View.GONE);
            }

            if (adapter.isHebbd()){
                showQualityLabel = true;
                mHebbd.setVisibility(View.VISIBLE);
                GradientDrawable background = (GradientDrawable) mHebbd.getBackground();
                background.setColor(adapter.getColor());
            }else{
                mHebbd.setVisibility(View.GONE);
            }

            if (adapter.isHect()){
                showQualityLabel = true;
                mHect.setVisibility(View.VISIBLE);
                GradientDrawable background = (GradientDrawable) mHect.getBackground();
                background.setColor(adapter.getColor());
            }else{
                mHect.setVisibility(View.GONE);
            }


            if (showQualityLabel){
                mQualityLabel.setTextColor(adapter.getColor());
                mQualityLabel.setVisibility(View.VISIBLE);
            }

        }

    }
}
