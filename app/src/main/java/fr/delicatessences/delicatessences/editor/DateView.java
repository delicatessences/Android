package fr.delicatessences.delicatessences.editor;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.fragments.DatePickerFragment;


public class DateView extends LinearLayout implements OnClickListener {


    private TextView mTextView;
    private LayoutInflater mInflater;
    private Button mButtonPopup;
    private Date mCurrentDate;
    private Date mInitialDate;


    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateView();
        getCustomAttributes(context, attrs);
    }



    public void setInitialDate(Date date){
        if (date != null){
            mCurrentDate = new Date(date.getTime());
        }
        mInitialDate = mCurrentDate;

        updateView();
    }


    public void setDate(long date){
        mCurrentDate = date > -1 ? new Date(date) : null;

        updateView();
    }


    public void setDate(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0);
        mCurrentDate = c.getTime();
        updateView();
    }





    public Date getDate(){
        if (mCurrentDate != null){
            return new Date(mCurrentDate.getTime());
        }

        return null;
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mTextView != null) {
            mTextView.setEnabled(enabled);
        }
    }



    private void updateView() {

        if (mInflater == null){
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.membership_view, this, true);
        }

        StringBuilder sb = new StringBuilder();
        if (mCurrentDate != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mCurrentDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String monthS = month < 10  ? String.format("%02d", month) : String.valueOf(month);
            sb.append(monthS).append("/").append(year);
        }

        if (mTextView == null) {
            mTextView = (TextView) findViewById(R.id.members_text_view);
            mTextView.setOnClickListener(this);
            mTextView.setTextColor(getResources().getColor(R.color.secondary_text_color));
            mTextView.setTextSize(15);
        }

        mTextView.setEnabled(isEnabled());
        mTextView.setText(sb);

        if (mButtonPopup == null){
            mButtonPopup = (Button) findViewById(R.id.button_popup);
            mButtonPopup.setOnClickListener(this);
        }



        setVisibility(VISIBLE);
    }




    @Override
    public void onClick(View v) {

        showDatePickerDialog();
    }



    private void showDatePickerDialog() {
        //if no date yet selected, use today's
        Date date = mCurrentDate;
        if (date == null){
            date = Calendar.getInstance().getTime();
        }

        DatePickerFragment newFragment = DatePickerFragment.newInstance(date);
        FragmentActivity activity = (FragmentActivity) getContext();
        FragmentManager fm = activity.getSupportFragmentManager();
        newFragment.show(fm, "datePicker");
    }


    private void getCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MembershipView, 0, 0);

        try {
            mTextView.setHint(typedArray.getString(R.styleable.MembershipView_android_hint));
        } finally {
            typedArray.recycle();
        }
    }


    public boolean hasChanged() {
        return mCurrentDate != null && !mCurrentDate.equals(mInitialDate);
    }

    public boolean isEmpty(){ return mCurrentDate == null; }

}
