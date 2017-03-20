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
import fr.delicatessences.delicatessences.model.VegetalIndication;
import fr.delicatessences.delicatessences.model.VegetalOil;
import fr.delicatessences.delicatessences.model.VegetalProperty;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.VOIndication;
import fr.delicatessences.delicatessences.model.VOProperty;

public abstract class EditActivity extends OrmLiteBaseActionBarActivity<DatabaseHelper> {


    private String mFeedbackMessage = null;
    private int mId;


    protected abstract int getLayout();

    protected int getId(){
        return mId;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_done);
        }

        SystemBarTintManager statusBarManager = new SystemBarTintManager(this);
        statusBarManager.setStatusBarTintEnabled(true);
        statusBarManager.setTintColor(getResources().getColor(R.color.primary_color));

        Intent intent = getIntent();
        mId = intent.getIntExtra(MainActivity.EXTRA_ID, 0);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveChanges();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected abstract void saveNew() throws SQLException;

    @Override
    public void onBackPressed() {
        if (hasChanged()){
            showDialog();
        } else {
            super.onBackPressed();
        }
    }


    protected void setFeedbackMessage(int message){
        mFeedbackMessage = getResources().getString(message);
    }


    public void saveChanges() {
        try {
            if (mId > 0) {
                saveModifications();
            } else {
                saveNew();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mFeedbackMessage = getResources().getString(R.string.save_failed);
        }
        showFeedbackMessage();
        super.onBackPressed();
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Resources resources = getResources();
        builder.setMessage(resources.getString(R.string.cancel_modifications));

        builder.setPositiveButton(resources.getString(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditActivity.super.onBackPressed();
                    }
                }
        );
        builder.setNegativeButton(resources.getString(R.string.action_cancel), null);
        builder.show();
    }


    protected abstract void saveModifications() throws SQLException;


    protected abstract boolean hasChanged();


    protected abstract boolean isEmpty();


    private void showFeedbackMessage() {
        if (mFeedbackMessage != null) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, mFeedbackMessage, duration);
            toast.show();
        }
    }
}
