package fr.delicatessences.delicatessences.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Date;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.CustomCheckBox;
import fr.delicatessences.delicatessences.editor.CustomEditText;
import fr.delicatessences.delicatessences.editor.DateView;
import fr.delicatessences.delicatessences.model.Bottle;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EssentialOil;

public class EditBottleActivity extends EditActivity
        implements DatePickerDialog.OnDateSetListener {


    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_MONTH = "month";
    public static final String EXTRA_DAY = "day";
    private static final String STATE_BRAND = "brand";
    private static final String STATE_PRICE = "price";
    private static final String STATE_VOLUME = "volume";
    private static final String STATE_ORIGIN = "origin";
    private static final String STATE_DATE = "date";
    private static final String STATE_PURE = "pure";
    private static final String STATE_BIO = "bio";
    private static final String STATE_HEBBD = "hebbd";
    private static final String STATE_HECT = "hect";


    private CustomEditText mBrandText;
    private CustomEditText mPriceText;
    private CustomEditText mVolumeText;
    private CustomEditText mOriginText;
    private DateView mDateView;
    private CustomCheckBox mBioCheck;
    private CustomCheckBox mPureCheck;
    private CustomCheckBox mHEBBDCheck;
    private CustomCheckBox mHECTCheck;
    private int mEssentialOilId;
    private Bottle mBottle;

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_bottle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mEssentialOilId = intent.getIntExtra(MainActivity.EXTRA_ESSENTIAL_OIL_ID, 0);
        int mId = getId();
        if (mId > 0) {
            setTitle(getResources().getString(R.string.title_edit_bottle));
            try {
                DatabaseHelper helper = getHelper();
                Dao<Bottle, Integer> dao = helper.getBottleDao();
                mBottle = dao.queryForId(mId);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        mBrandText = (CustomEditText) findViewById(R.id.brand);
        mPriceText = (CustomEditText) findViewById(R.id.price);
        mVolumeText = (CustomEditText) findViewById(R.id.capacity);
        mOriginText = (CustomEditText) findViewById(R.id.origin);
        mDateView = (DateView) findViewById(R.id.date);
        mBioCheck = (CustomCheckBox) findViewById(R.id.bio);
        mPureCheck = (CustomCheckBox) findViewById(R.id.pure);
        mHEBBDCheck = (CustomCheckBox) findViewById(R.id.hebbd);
        mHECTCheck = (CustomCheckBox) findViewById(R.id.hect);

        initializeView();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_BRAND, mBrandText.getText().toString());
        savedInstanceState.putString(STATE_PRICE, mPriceText.getText().toString());
        savedInstanceState.putString(STATE_VOLUME, mVolumeText.getText().toString());
        savedInstanceState.putString(STATE_ORIGIN, mOriginText.getText().toString());
        Date date = mDateView.getDate();
        savedInstanceState.putLong(STATE_DATE, date != null ? date.getTime() : -1);

        savedInstanceState.putBoolean(STATE_BIO, mBioCheck.isChecked());
        savedInstanceState.putBoolean(STATE_PURE, mPureCheck.isChecked());
        savedInstanceState.putBoolean(STATE_HEBBD, mHEBBDCheck.isChecked());
        savedInstanceState.putBoolean(STATE_HECT, mHECTCheck.isChecked());

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mBrandText.setText(savedInstanceState.getString(STATE_BRAND), false);
        mPriceText.setText(savedInstanceState.getString(STATE_PRICE), false);
        mVolumeText.setText(savedInstanceState.getString(STATE_VOLUME), false);
        mOriginText.setText(savedInstanceState.getString(STATE_ORIGIN), false);
        mDateView.setDate(savedInstanceState.getLong(STATE_DATE));
        mBioCheck.setChecked(savedInstanceState.getBoolean(STATE_BIO), false);
        mPureCheck.setChecked(savedInstanceState.getBoolean(STATE_PURE), false);
        mHEBBDCheck.setChecked(savedInstanceState.getBoolean(STATE_HEBBD), false);
        mHECTCheck.setChecked(savedInstanceState.getBoolean(STATE_HECT), false);
    }




    private void initializeView() {

        //edit mode
        if (mBottle != null) {
            mBrandText.setText(mBottle.getBrand(), true);
            mOriginText.setText(mBottle.getOrigin(), true);

            Double price = mBottle.getPrice();
            mPriceText.setText(price != null ? String.valueOf(price) : "", true);

            Integer capacity = mBottle.getCapacity();
            mVolumeText.setText(capacity != null ? String.valueOf(capacity) : "", true);

            mDateView.setInitialDate(mBottle.getExpiration());

            mBioCheck.setChecked(mBottle.isBio(), true);
            mPureCheck.setChecked(mBottle.isPure(), true);
            mHEBBDCheck.setChecked(mBottle.isHebbd(), true);
            mHECTCheck.setChecked(mBottle.isHect(), true);
        }
    }

    @Override
    protected void saveNew() throws SQLException {

        //user entered nothing - do nothing
        if (isEmpty()) {
            return;
        }

        //get the brand
        String brand = mBrandText.getText().toString();

        //get the price
        String priceS = mPriceText.getText().toString();
        Double price = priceS.isEmpty() ? null : Double.valueOf(priceS);

        //get the volume
        String volumeS = mVolumeText.getText().toString();
        Integer volume = volumeS.isEmpty() ? null : Integer.valueOf(volumeS);

        //get the origin
        String origin = mOriginText.getText().toString();

        //get the expiration date
        Date expirationDate = mDateView.getDate();

        //is bio?
        boolean bio = mBioCheck.isChecked();

        //is pure?
        boolean pure = mPureCheck.isChecked();

        //is hebbd?
        boolean hebbd = mHEBBDCheck.isChecked();

        //is hect?
        boolean hect = mHECTCheck.isChecked();

        //save to database
        final DatabaseHelper helper = getHelper();
        Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
        EssentialOil essentialOil = essentialOilDao.queryForId(mEssentialOilId);

        if (essentialOil == null) {
            throw new SQLException("Cannot find essential oil with id " + mEssentialOilId + " in database, aborting modifications");
        }

        final Bottle bottle = new Bottle(brand, price, volume, expirationDate, origin,
                bio, pure, hect, hebbd, essentialOil);

        setFeedbackMessage(R.string.save_bottle);

        Dao<Bottle, Integer> dao = helper.getBottleDao();
        dao.create(bottle);

        addToIndex(bottle, essentialOil);

    }

    private void addToIndex(Bottle bottle, EssentialOil essentialOil) {

        String description = essentialOil.getDescription();

        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(essentialOil.getName(), bottle.getBrand()))
                .setText(description != null ? description : "")
                .setUrl(bottle.getUrl())
                .build();

        FirebaseAppIndex.getInstance().update(indexable);
    }

    private String getIndexableName(String name, String brand){
        StringBuilder sb = new StringBuilder();
        Resources resources = getResources();
        String withoutBrand = resources.getString(R.string.without_brand);
        sb.append(resources.getString(R.string.bottle_of));
        sb.append(name != null ? name : "");
        sb.append(" " + ((brand != null && brand.length() > 0) ? "(" + brand + ")" : withoutBrand));
        return sb.toString();
    }



    private void updateIndex(EssentialOil essentialOil, String brand){

        String description = essentialOil.getDescription();

        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(essentialOil.getName(), brand))
                .setText(description != null ? description : "")
                .setUrl(mBottle.getUrl())
                .build();

        FirebaseAppIndex.getInstance().update(indexable);
    }


    @Override
    protected void saveModifications() throws SQLException {

        if (!hasChanged()) {
            return;
        }

        int mId = getId();
        //save to database
        final DatabaseHelper helper = getHelper();
        Dao<Bottle, Integer> dao = helper.getBottleDao();
        UpdateBuilder<Bottle, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Bottle.ID_FIELD_NAME, mId);
        boolean canUpdate = false;
        if (mBrandText.hasChanged()) {
            SelectArg selectArg = new SelectArg(mBrandText.getText().toString());
            updateBuilder.updateColumnValue(Bottle.BRAND_FIELD_NAME, selectArg);
            canUpdate = true;
        }
        if (mPriceText.hasChanged()) {
            String priceS = mPriceText.getText().toString();
            Double price = priceS.isEmpty() ? null : Double.valueOf(priceS);
            updateBuilder.updateColumnValue(Bottle.PRICE_FIELD_NAME, price);
            canUpdate = true;
        }
        if (mVolumeText.hasChanged()) {
            String volumeS = mVolumeText.getText().toString();
            Integer volume = volumeS.isEmpty() ? null : Integer.valueOf(volumeS);
            updateBuilder.updateColumnValue(Bottle.CAPACITY_FIELD_NAME, volume);
            canUpdate = true;
        }
        if (mOriginText.hasChanged()) {
            SelectArg selectArg = new SelectArg(mOriginText.getText().toString());
            updateBuilder.updateColumnValue(Bottle.ORIGIN_FIELD_NAME, selectArg);
            canUpdate = true;
        }
        if (mDateView.hasChanged()) {
            updateBuilder.updateColumnValue(Bottle.EXPIRATION_DATE_FIELD_NAME, mDateView.getDate());
            canUpdate = true;
        }
        if (mBioCheck.hasChanged()) {
            updateBuilder.updateColumnValue(Bottle.BIO_FIELD_NAME, mBioCheck.isChecked());
            canUpdate = true;
        }
        if (mPureCheck.hasChanged()) {
            updateBuilder.updateColumnValue(Bottle.PURE_FIELD_NAME, mPureCheck.isChecked());
            canUpdate = true;
        }
        if (mHECTCheck.hasChanged()) {
            updateBuilder.updateColumnValue(Bottle.HECT_FIELD_NAME, mHECTCheck.isChecked());
            canUpdate = true;
        }
        if (mHEBBDCheck.hasChanged()) {
            updateBuilder.updateColumnValue(Bottle.HEBBD_FIELD_NAME, mHEBBDCheck.isChecked());
            canUpdate = true;
        }
        if (canUpdate) {
            updateBuilder.update();
        }

        setFeedbackMessage(R.string.edit_bottle);

        Dao<EssentialOil, Integer> essentialOilDao = helper.getEssentialOilDao();
        int essentialOilId = mBottle.getEssentialOil().getId();
        EssentialOil essentialOil = essentialOilDao.queryForId(essentialOilId);
        updateIndex(essentialOil, mBrandText.getText().toString());

    }

    @Override
    protected boolean hasChanged() {

        return mBrandText.hasChanged() ||
                mPriceText.hasChanged() ||
                mVolumeText.hasChanged() ||
                mOriginText.hasChanged() ||
                mDateView.hasChanged() ||
                mBioCheck.hasChanged() ||
                mPureCheck.hasChanged() ||
                mHEBBDCheck.hasChanged() ||
                mHECTCheck.hasChanged();
    }

    @Override
    protected boolean isEmpty() {
        return mBrandText.isEmpty() &&
                mPriceText.isEmpty() &&
                mVolumeText.isEmpty() &&
                mOriginText.isEmpty() &&
                mDateView.isEmpty() &&
                !mBioCheck.isChecked() &&
                !mPureCheck.isChecked() &&
                !mHECTCheck.isChecked() &&
                !mHEBBDCheck.isChecked();
    }



    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDateView.setDate(dayOfMonth, monthOfYear, year);
    }
}
