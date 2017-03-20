package fr.delicatessences.delicatessences.fragments;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.SimpleCursorAdapter;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.model.DatabaseHelper;

public class ChooseOilDialogFragment extends DialogFragment {

    private SimpleCursorAdapter mAdapter;
    private NoticeDialogListener mListener;
    private int mPosition;


    public interface NoticeDialogListener {
        void onDialogPositiveClick(int id);
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPosition = -1;

        MainActivity activity = (MainActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            DatabaseHelper helper = activity.getHelper();
            Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
            QueryBuilder<EssentialOil, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(EssentialOil.NAME_FIELD_NAME, true);
            CloseableIterator<EssentialOil> iterator = dao.iterator(queryBuilder.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
            final Cursor cursor = results.getRawCursor();
            builder.setSingleChoiceItems(cursor, -1, EssentialOil.NAME_FIELD_NAME, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mPosition = id;
                        }
                    }
            );

            if (cursor != null && cursor.getCount() > 0){
                builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mPosition > -1){
                            cursor.moveToPosition(mPosition);
                            int index = cursor.getColumnIndex(EssentialOil.ID_FIELD_NAME);
                            int essentialOilId = cursor.getInt(index);
                            mListener.onDialogPositiveClick(essentialOilId);
                        }
                    }
                });
                builder.setTitle(R.string.pick_essential_oil);
            }
            else{
                builder.setTitle(R.string.no_essential_oil);
                builder.setMessage(R.string.how_to_add_bottle_when_no_essential_oil);
            }

            builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing
                }
            });



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

    }
}
