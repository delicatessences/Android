package fr.delicatessences.delicatessences.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import fr.delicatessences.delicatessences.activities.EditBottleActivity;

public class DatePickerFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        int year = arguments.getInt(EditBottleActivity.EXTRA_YEAR);
        int month = arguments.getInt(EditBottleActivity.EXTRA_MONTH);
        int day = arguments.getInt(EditBottleActivity.EXTRA_DAY);

        Activity activity = getActivity();
        DatePickerDialog pickerDialog = new DatePickerDialog(activity,
                (DatePickerDialog.OnDateSetListener) activity, year, month, day);
        pickerDialog.getDatePicker().setCalendarViewShown(false);
        return pickerDialog;
    }


    public static DatePickerFragment newInstance(Date date){
        DatePickerFragment fragment = new DatePickerFragment();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Bundle args = new Bundle();
        args.putInt(EditBottleActivity.EXTRA_YEAR, year);
        args.putInt(EditBottleActivity.EXTRA_MONTH, month);
        args.putInt(EditBottleActivity.EXTRA_DAY, day);
        fragment.setArguments(args);

        return fragment;
    }




}