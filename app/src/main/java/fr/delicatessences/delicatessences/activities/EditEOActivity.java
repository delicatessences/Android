package fr.delicatessences.delicatessences.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.editor.CustomEditText;
import fr.delicatessences.delicatessences.editor.MembershipView;
import fr.delicatessences.delicatessences.fragments.ViewType;
import fr.delicatessences.delicatessences.model.Administration;
import fr.delicatessences.delicatessences.model.EssentialIndication;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.EssentialProperty;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EOAdministration;
import fr.delicatessences.delicatessences.model.EOIndication;
import fr.delicatessences.delicatessences.model.EOProperty;

public class EditEOActivity extends EditActivity {

    private static final String STATE_NAME = "name";
    private static final String STATE_BOTANICAL_NAME = "botanical_name";
    private static final String STATE_DISTILLED_ORGAN = "distilled_organ";
    private static final String STATE_DESCRIPTION = "description";
    private static final String STATE_PRECAUTIONS = "precautions";
    private static final String STATE_CHEMOTYPE = "chemotype";
    private static final String STATE_PROPERTIES = "properties";
    private static final String STATE_INDICATIONS = "indications";
    private static final String STATE_ADMINISTRATIONS = "administrations";

    private CustomEditText mNameText;
    private CustomEditText mDecriptionText;
    private MembershipView mPropertiesView;
    private MembershipView mIndicationsView;
    private CustomEditText mBotanicalNameText;
    private MembershipView mAdministrationsView;
    private CustomEditText mDistilledOrganText;
    private CustomEditText mChemotypeText;
    private CustomEditText mPrecautionsText;
    private EssentialOil mEssentialOil;

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_eo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getId();
        if (id > 0) {
            setTitle(getResources().getString(R.string.title_edit_eo));
            try {
                DatabaseHelper helper = getHelper();
                Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
                mEssentialOil = dao.queryForId(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        mNameText = (CustomEditText) findViewById(R.id.name);
        mBotanicalNameText = (CustomEditText) findViewById(R.id.botanical_name);
        mDistilledOrganText = (CustomEditText) findViewById(R.id.distilled_organ);
        mChemotypeText = (CustomEditText) findViewById(R.id.chemotype);
        mDecriptionText = (CustomEditText) findViewById(R.id.description);
        mPropertiesView = (MembershipView) findViewById(R.id.properties_view);
        mIndicationsView = (MembershipView) findViewById(R.id.indications_view);
        mAdministrationsView = (MembershipView) findViewById(R.id.administrations_view);
        mPrecautionsText = (CustomEditText) findViewById(R.id.precautions);

        initializeView();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_NAME, mNameText.getText().toString());
        savedInstanceState.putString(STATE_BOTANICAL_NAME, mBotanicalNameText.getText().toString());
        savedInstanceState.putString(STATE_CHEMOTYPE, mChemotypeText.getText().toString());
        savedInstanceState.putString(STATE_DESCRIPTION, mDecriptionText.getText().toString());
        savedInstanceState.putString(STATE_DISTILLED_ORGAN, mDistilledOrganText.getText().toString());
        savedInstanceState.putString(STATE_PRECAUTIONS, mPrecautionsText.getText().toString());
        savedInstanceState.putIntegerArrayList(STATE_PROPERTIES, mPropertiesView.getMembers());
        savedInstanceState.putIntegerArrayList(STATE_INDICATIONS, mIndicationsView.getMembers());
        savedInstanceState.putIntegerArrayList(STATE_ADMINISTRATIONS, mAdministrationsView.getMembers());

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mNameText.setText(savedInstanceState.getString(STATE_NAME), false);
        mBotanicalNameText.setText(savedInstanceState.getString(STATE_BOTANICAL_NAME), false);
        mPrecautionsText.setText(savedInstanceState.getString(STATE_PRECAUTIONS), false);
        mDistilledOrganText.setText(savedInstanceState.getString(STATE_DISTILLED_ORGAN), false);
        mChemotypeText.setText(savedInstanceState.getString(STATE_CHEMOTYPE), false);
        mDecriptionText.setText(savedInstanceState.getString(STATE_DESCRIPTION), false);
        mPropertiesView.setMembers(savedInstanceState.getIntegerArrayList(STATE_PROPERTIES), false);
        mIndicationsView.setMembers(savedInstanceState.getIntegerArrayList(STATE_INDICATIONS), false);
        mAdministrationsView.setMembers(savedInstanceState.getIntegerArrayList(STATE_ADMINISTRATIONS), false);

    }


    private void initializeView() {

        DatabaseHelper helper = getHelper();

        try {
            Dao<EssentialProperty, Integer> dao = helper.getEssentialPropertyDao();
            QueryBuilder<EssentialProperty, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(EssentialProperty.NAME_FIELD_NAME, true);
            CloseableIterator<EssentialProperty> propertyIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) propertyIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mPropertiesView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Dao<EssentialIndication, Integer> dao = helper.getEssentialIndicationDao();
            QueryBuilder<EssentialIndication, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(EssentialIndication.NAME_FIELD_NAME, true);
            CloseableIterator<EssentialIndication> indicationsIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) indicationsIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mIndicationsView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            Dao<Administration, Integer> dao = helper.getAdministrationDao();
            QueryBuilder<Administration, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Administration.NAME_FIELD_NAME, true);
            CloseableIterator<Administration> administrationIterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) administrationIterator.getRawResults();
            Cursor cursor = results.getRawCursor();
            mAdministrationsView.setMetaData(cursor);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (mEssentialOil != null) {
            mNameText.setText(mEssentialOil.getName(), true);
            mBotanicalNameText.setText(mEssentialOil.getBotanicalName(), true);
            mDistilledOrganText.setText(mEssentialOil.getDistilledOrgan(), true);
            mChemotypeText.setText(mEssentialOil.getChemotype(), true);
            mDecriptionText.setText(mEssentialOil.getDescription(), true);
            mPrecautionsText.setText(mEssentialOil.getPrecautions(), true);
            int id = getId();

            try {
                Dao<EOProperty, Integer> eoPropertyDao = helper.getEOPropertyDao();
                List<EOProperty> eoProperties = eoPropertyDao.queryForEq(EOProperty.OIL_ID, id);
                ArrayList<Integer> properties = new ArrayList<>();
                for (EOProperty p : eoProperties) {
                    properties.add(p.getProperty().getId());
                }
                mPropertiesView.setMembers(properties, true);


                Dao<EOIndication, Integer> eoIndicationDao = helper.getEOIndicationDao();
                List<EOIndication> eoIndications = eoIndicationDao.queryForEq(EOIndication.OIL_ID, id);
                ArrayList<Integer> indications = new ArrayList<>();
                for (EOIndication i : eoIndications) {
                    indications.add(i.getIndication().getId());
                }
                mIndicationsView.setMembers(indications, true);

                Dao<EOAdministration, Integer> eoAdministrationDao = helper.getEOAdministrationDao();
                List<EOAdministration> eoAdministrations = eoAdministrationDao.queryForEq(EOAdministration.OIL_ID, id);
                ArrayList<Integer> administrations = new ArrayList<>();
                for (EOAdministration a : eoAdministrations) {
                    administrations.add(a.getAdministration().getId());
                }
                mAdministrationsView.setMembers(administrations, true);

            } catch (SQLException e) {
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

        //get the botanical name
        String botanicalName = mBotanicalNameText.getText().toString();

        //get the distilled organ
        String distilledOrgan = mDistilledOrganText.getText().toString();

        //get the chemotype
        String chemotype = mChemotypeText.getText().toString();

        //get the description
        String description = mDecriptionText.getText().toString();

        //get the properties
        List<Integer> properties = mPropertiesView.getMembers();

        //get the indications
        List<Integer> indications = mIndicationsView.getMembers();

        //get the administrations
        List<Integer> administrations = mAdministrationsView.getMembers();

        //get the description
        String precautions = mPrecautionsText.getText().toString();


        //save to database
        final DatabaseHelper helper = getHelper();
        final EssentialOil essentialOil = new EssentialOil(name, botanicalName, description,
                distilledOrgan, chemotype, precautions);
        final List<EssentialProperty> essentialProperties = helper.getEssentialProperties(properties);
        final List<EssentialIndication> essentialIndications = helper.getEssentialIndications(indications);
        final List<Administration> essentialAdministrations = helper.getAdministrations(administrations);
        setFeedbackMessage(R.string.save_essential_oil);

        TransactionManager.callInTransaction(helper.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {

                        Dao<EssentialOil, Integer> oilDao = helper.getEssentialOilDao();
                        oilDao.create(essentialOil);

                        Dao<EOProperty, Integer> eoPropertyDao = helper.getEOPropertyDao();
                        for (EssentialProperty essentialProperty : essentialProperties) {
                            EOProperty eoProperty = new EOProperty(essentialOil, essentialProperty);
                            eoPropertyDao.create(eoProperty);
                        }

                        Dao<EOIndication, Integer> eoIndicationDao = helper.getEOIndicationDao();
                        for (EssentialIndication essentialIndication : essentialIndications) {
                            EOIndication eoIndication = new EOIndication(essentialOil, essentialIndication);
                            eoIndicationDao.create(eoIndication);
                        }

                        Dao<EOAdministration, Integer> eoAdministrationDao = helper.getEOAdministrationDao();
                        for (Administration essentialAdministration : essentialAdministrations) {
                            EOAdministration eoAdministration = new EOAdministration(essentialOil, essentialAdministration);
                            eoAdministrationDao.create(eoAdministration);
                        }

                        return null;
                    }
                });
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
                        Dao<EssentialOil, Integer> oilDao = helper.getEssentialOilDao();
                        UpdateBuilder<EssentialOil, Integer> updateBuilder = oilDao.updateBuilder();
                        updateBuilder.where().eq(EssentialOil.ID_FIELD_NAME, id);
                        boolean canUpdate = false;
                        if (mNameText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mNameText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.NAME_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mBotanicalNameText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mBotanicalNameText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.BOTANICAL_NAME_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mDistilledOrganText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mDistilledOrganText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.DISTILLED_ORGAN_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mChemotypeText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mChemotypeText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.CHEMOTYPE_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mDecriptionText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mDecriptionText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.DESCRIPTION_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (mPrecautionsText.hasChanged()) {
                            SelectArg selectArg = new SelectArg(mPrecautionsText.getText().toString());
                            updateBuilder.updateColumnValue(EssentialOil.PRECAUTIONS_FIELD_NAME, selectArg);
                            canUpdate = true;
                        }
                        if (canUpdate) {
                            updateBuilder.update();
                        }


                        EssentialOil essentialOil = oilDao.queryForId(id);
                        if (essentialOil == null) {
                            throw new NullPointerException("Cannot find essential oil with id " + id + " in database, aborting modifications");
                        }

                        if (mPropertiesView.hasChanged()) {
                            List<Integer> added = mPropertiesView.getAdded();
                            List<EssentialProperty> addedProperties = helper.getEssentialProperties(added);
                            Dao<EOProperty, Integer> eoPropertyDao = helper.getEOPropertyDao();
                            for (EssentialProperty essentialProperty : addedProperties) {
                                EOProperty eoProperty = new EOProperty(essentialOil, essentialProperty);
                                eoPropertyDao.create(eoProperty);
                            }

                            List<Integer> removed = mPropertiesView.getRemoved();
                            DeleteBuilder<EOProperty, Integer> deleteBuilder = eoPropertyDao.deleteBuilder();
                            deleteBuilder.where().eq(EOProperty.OIL_ID, id).and().in(EOProperty.PROPERTY_ID, removed);
                            deleteBuilder.delete();

                        }

                        if (mIndicationsView.hasChanged()) {
                            List<Integer> added = mIndicationsView.getAdded();
                            List<EssentialIndication> addedIndications = helper.getEssentialIndications(added);
                            Dao<EOIndication, Integer> eoIndicationDao = helper.getEOIndicationDao();
                            for (EssentialIndication essentialIndication : addedIndications) {
                                EOIndication eoIndication = new EOIndication(essentialOil, essentialIndication);
                                eoIndicationDao.create(eoIndication);
                            }

                            List<Integer> removed = mIndicationsView.getRemoved();
                            DeleteBuilder<EOIndication, Integer> deleteBuilder = eoIndicationDao.deleteBuilder();
                            deleteBuilder.where().eq(EOIndication.OIL_ID, id).and().in(EOIndication.INDICATION_ID, removed);
                            deleteBuilder.delete();

                        }

                        if (mAdministrationsView.hasChanged()) {
                            List<Integer> added = mAdministrationsView.getAdded();
                            List<Administration> addedAdministrations = helper.getAdministrations(added);
                            Dao<EOAdministration, Integer> eoAdministrationDao = helper.getEOAdministrationDao();
                            for (Administration addedAdministration : addedAdministrations) {
                                EOAdministration eoAdministration = new EOAdministration(essentialOil, addedAdministration);
                                eoAdministrationDao.create(eoAdministration);
                            }

                            List<Integer> removed = mAdministrationsView.getRemoved();
                            DeleteBuilder<EOAdministration, Integer> deleteBuilder = eoAdministrationDao.deleteBuilder();
                            deleteBuilder.where().eq(EOAdministration.OIL_ID, id).and().in(EOAdministration.ADMINISTRATION_ID, removed);
                            deleteBuilder.delete();

                        }

                        return null;
                    }
                });


        setFeedbackMessage(R.string.edit_essential_oil);


    }

    @Override
    protected boolean hasChanged() {

        return mNameText.hasChanged() ||
                mBotanicalNameText.hasChanged() ||
                mDistilledOrganText.hasChanged() ||
                mChemotypeText.hasChanged() ||
                mDecriptionText.hasChanged() ||
                mPropertiesView.hasChanged() ||
                mIndicationsView.hasChanged() ||
                mAdministrationsView.hasChanged() ||
                mPrecautionsText.hasChanged();
    }

    @Override
    protected boolean isEmpty() {
        return mNameText.isEmpty() &&
                mBotanicalNameText.isEmpty() &&
                mDistilledOrganText.isEmpty() &&
                mChemotypeText.isEmpty() &&
                mDecriptionText.isEmpty() &&
                mPropertiesView.isEmpty() &&
                mIndicationsView.isEmpty() &&
                mAdministrationsView.isEmpty() &&
                mPrecautionsText.isEmpty();
    }


}
