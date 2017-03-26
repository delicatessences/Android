package fr.delicatessences.delicatessences.activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.CustomEditText;
import fr.delicatessences.delicatessences.editor.MembershipView;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.VOIndication;
import fr.delicatessences.delicatessences.model.VOProperty;
import fr.delicatessences.delicatessences.model.VegetalIndication;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.VegetalProperty;

public class EditVOActivity extends EditActivity {

    private static final String STATE_NAME = "name";
    private static final String STATE_DESCRIPTION = "description";
    private static final String STATE_PROPERTIES = "properties";
    private static final String STATE_INDICATIONS = "indications";

    private CustomEditText mNameText;
    private CustomEditText mDecriptionText;
    private MembershipView mPropertiesView;
    private MembershipView mIndicationsView;
    private VegetalOil mVegetalOil;


    @Override
    protected int getLayout() {
        return R.layout.activity_edit_vo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getId();
        if (id > 0) {
            setTitle(getResources().getString(R.string.title_edit_vo));
            try {
                DatabaseHelper helper = getHelper();
                Dao<VegetalOil, Integer> dao = helper.getVegetalOilDao();
                mVegetalOil = dao.queryForId(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        mNameText = (CustomEditText) findViewById(R.id.name);
        mDecriptionText = (CustomEditText) findViewById(R.id.description);
        mPropertiesView = (MembershipView) findViewById(R.id.properties_view);
        mIndicationsView = (MembershipView) findViewById(R.id.indications_view);

        initializeView();

    }





    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_NAME, mNameText.getText().toString());
        savedInstanceState.putString(STATE_DESCRIPTION, mDecriptionText.getText().toString());
        savedInstanceState.putIntegerArrayList(STATE_PROPERTIES, mPropertiesView.getMembers());
        savedInstanceState.putIntegerArrayList(STATE_INDICATIONS, mIndicationsView.getMembers());

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mNameText.setText(savedInstanceState.getString(STATE_NAME), false);
        mDecriptionText.setText(savedInstanceState.getString(STATE_DESCRIPTION), false);
        mPropertiesView.setMembers(savedInstanceState.getIntegerArrayList(STATE_PROPERTIES), false);
        mIndicationsView.setMembers(savedInstanceState.getIntegerArrayList(STATE_INDICATIONS), false);
    }




    private void initializeView() {

        DatabaseHelper helper = getHelper();

        try {
            Dao<VegetalProperty, Integer> dao = helper.getVegetalPropertyDao();
            QueryBuilder<VegetalProperty, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(VegetalProperty.NAME_FIELD_NAME, true);
            CloseableIterator<VegetalProperty> propertyIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) propertyIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mPropertiesView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Dao<VegetalIndication, Integer> dao = helper.getVegetalIndicationDao();
            QueryBuilder<VegetalIndication, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(VegetalIndication.NAME_FIELD_NAME, true);
            CloseableIterator<VegetalIndication> indicationsIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) indicationsIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mIndicationsView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //edit mode
        if (mVegetalOil != null) {
            int id = getId();
            mNameText.setText(mVegetalOil.getName(), true);
            mDecriptionText.setText(mVegetalOil.getDescription(), true);

            try {
                Dao<VOProperty, Integer> voPropertyDao = helper.getVOPropertyDao();
                List<VOProperty> voProperties = voPropertyDao.queryForEq(VOProperty.OIL_ID, id);
                ArrayList<Integer> properties = new ArrayList<>();
                for (VOProperty p : voProperties) {
                    properties.add(p.getProperty().getId());
                }
                mPropertiesView.setMembers(properties, true);

                Dao<VOIndication, Integer> voIndicationDao = helper.getVOIndicationDao();
                List<VOIndication> voIndications = voIndicationDao.queryForEq(VOIndication.OIL_ID, id);
                ArrayList<Integer> indications = new ArrayList<>();
                for (VOIndication i : voIndications) {
                    indications.add(i.getIndication().getId());
                }
                mIndicationsView.setMembers(indications, true);
            }
            catch(SQLException e){
                e.printStackTrace();
            }

        }

    }


    @Override
    protected void saveNew() throws SQLException {

        //user entered nothing - do nothing
        if (isEmpty()) {
            return;
        }

        //get the name
        String name = mNameText.getText().toString();

        //get the description
        String description = mDecriptionText.getText().toString();

        //get the properties
        List<Integer> properties = mPropertiesView.getMembers();

        //get the indications
        List<Integer> indications = mIndicationsView.getMembers();


        //save to database
        final DatabaseHelper helper = getHelper();
        final VegetalOil vegetalOil = new VegetalOil(name, description);
        final List<VegetalProperty> vegetalProperties = helper.getVegetalProperties(properties);
        final List<VegetalIndication> vegetalIndications = helper.getVegetalIndications(indications);
        setFeedbackMessage(R.string.save_vegetal_oil);

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {

                        Dao<VegetalOil, Integer> oilDao = helper.getVegetalOilDao();
                        oilDao.create(vegetalOil);

                        Dao<VOProperty, Integer> voPropertyDao = helper.getVOPropertyDao();
                        for (VegetalProperty vegetalProperty : vegetalProperties) {
                            VOProperty voProperty = new VOProperty(vegetalOil, vegetalProperty);
                            voPropertyDao.create(voProperty);
                        }

                        Dao<VOIndication, Integer> voIndicationDao = helper.getVOIndicationDao();
                        for (VegetalIndication vegetalIndication : vegetalIndications) {
                            VOIndication voIndication = new VOIndication(vegetalOil, vegetalIndication);
                            voIndicationDao.create(voIndication);
                        }

                        return null;
                    }
                });

        addToIndex(vegetalOil);
    }



    @Override
    protected void saveModifications() throws SQLException {

        if (!hasChanged()) {
            return;
        }

        //save to database
        final DatabaseHelper helper = getHelper();
        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        int id = getId();
                        Dao<VegetalOil, Integer> oilDao = helper.getVegetalOilDao();
                        UpdateBuilder<VegetalOil, Integer> updateBuilder = oilDao.updateBuilder();
                        updateBuilder.where().eq(VegetalOil.ID_FIELD_NAME, id);
                        boolean canUpdate = false;
                        if (mNameText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mNameText.getText().toString());
                            updateBuilder.updateColumnValue(VegetalOil.NAME_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mDecriptionText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mDecriptionText.getText().toString());
                            updateBuilder.updateColumnValue(VegetalOil.DESCRIPTION_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (canUpdate) {
                            updateBuilder.update();
                        }


                        VegetalOil vegetalOil = oilDao.queryForId(id);
                        if (vegetalOil == null) {
                            throw new NullPointerException("Cannot find vegetal oil with id " + id + " in database, aborting modifications");
                        }

                        if (mPropertiesView.hasChanged()) {
                            List<Integer> added = mPropertiesView.getAdded();
                            List<VegetalProperty> addedProperties = helper.getVegetalProperties(added);
                            Dao<VOProperty, Integer> voPropertyDao = helper.getVOPropertyDao();
                            for (VegetalProperty vegetalProperty : addedProperties) {
                                VOProperty voProperty = new VOProperty(vegetalOil, vegetalProperty);
                                voPropertyDao.create(voProperty);
                            }

                            List<Integer> removed = mPropertiesView.getRemoved();
                            DeleteBuilder<VOProperty, Integer> deleteBuilder = voPropertyDao.deleteBuilder();
                            deleteBuilder.where().eq(VOProperty.OIL_ID, id).and().in(VOProperty.PROPERTY_ID, removed);
                            deleteBuilder.delete();

                        }

                        if (mIndicationsView.hasChanged()) {
                            List<Integer> added = mIndicationsView.getAdded();
                            List<VegetalIndication> addedIndications = helper.getVegetalIndications(added);
                            Dao<VOIndication, Integer> voIndicationDao = helper.getVOIndicationDao();
                            for (VegetalIndication vegetalIndication : addedIndications) {
                                VOIndication voIndication = new VOIndication(vegetalOil, vegetalIndication);
                                voIndicationDao.create(voIndication);
                            }

                            List<Integer> removed = mIndicationsView.getRemoved();
                            DeleteBuilder<VOIndication, Integer> deleteBuilder = voIndicationDao.deleteBuilder();
                            deleteBuilder.where().eq(VOIndication.OIL_ID, id).and().in(VOIndication.INDICATION_ID, removed);
                            deleteBuilder.delete();

                        }

                        return null;
                    }
                });


        setFeedbackMessage(R.string.edit_vegetal_oil);
        updateIndex(mNameText.getText().toString(), mDecriptionText.getText().toString());

    }


    private void addToIndex(VegetalOil vegetalOil) {

        String description = vegetalOil.getDescription();

        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(vegetalOil.getName()))
                .setText(description != null ? description : "")
                .setUrl(vegetalOil.getUrl())
                .build();

        FirebaseAppIndex.getInstance().update(indexable);
    }

    private String getIndexableName(String name){
        StringBuilder sb = new StringBuilder();
        Resources resources = getResources();
        sb.append(resources.getString(R.string.vo_of));
        sb.append(name != null ? name : "");
        return sb.toString();
    }

    private void updateIndex(String name, String description) {
        Indexable indexable = Indexables.noteDigitalDocumentBuilder()
                .setName(getIndexableName(name))
                .setText(description != null ? description : "")
                .setUrl(mVegetalOil.getUrl())
                .build();
    }


    @Override
    protected boolean hasChanged() {

        return mNameText.hasChanged() ||
                mDecriptionText.hasChanged() ||
                mPropertiesView.hasChanged() ||
                mIndicationsView.hasChanged();
    }

    @Override
    protected boolean isEmpty() {
        return mNameText.isEmpty() &&
                mDecriptionText.isEmpty() &&
                mPropertiesView.isEmpty() &&
                mIndicationsView.isEmpty();
    }


}
