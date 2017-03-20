package fr.delicatessences.delicatessences.editor;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.adapters.SpinnerArrayAdapter;
import fr.delicatessences.delicatessences.adapters.SpinnerArrayAdapter.SpinnerItem;


public class SpinnerWithHint extends LinearLayout
        implements AdapterView.OnItemSelectedListener {


    private static final String NAME_COLUMN = "name";
    private static final String ID_COLUMN = "_id";
    private String mHint;
    private Spinner mSpinner;
    private Cursor mCursor;
    private int mInitialSelection;
    private int mCurrentSelection;
    private LayoutInflater mInflater;


    public SpinnerWithHint(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInitialSelection = 0;
        mCurrentSelection = mInitialSelection;
        updateView();
        getCustomAttributes(context, attrs);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCurrentSelection = (int) parent.getItemIdAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mSpinner != null) {
            mSpinner.setEnabled(enabled);
        }
    }


    public void setMetaData(Cursor metaData) {
        this.mCursor = metaData;
        updateView();
    }


    public void setSelection(int id, boolean initial) {
        mCurrentSelection = id;
        if (initial){
            mInitialSelection = mCurrentSelection;
        }

        updateView();
    }


    private void updateView() {

        if (mInflater == null) {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.spinner_view, this, true);
        }

        if (mSpinner == null) {
            mSpinner = (Spinner) findViewById(R.id.spinner);
            mSpinner.setOnItemSelectedListener(this);
        }

        SpinnerArrayAdapter mAdapter = new SpinnerArrayAdapter(getContext()) {


        };


        if (mCursor != null) {
            mCursor.moveToPosition(-1);
            int nameColumn = mCursor.getColumnIndex(NAME_COLUMN);
            int idColumn = mCursor.getColumnIndex(ID_COLUMN);

            while (mCursor.moveToNext()) {
                String name = mCursor.getString(nameColumn);
                int id = mCursor.getInt(idColumn);
                mAdapter.add(new SpinnerItem(id, name));
            }
            mAdapter.add(new SpinnerItem(0, mHint));
        }

        mAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinner.setAdapter(mAdapter);
        if (mCurrentSelection == 0) {
            mSpinner.setSelection(mAdapter.getCount());
        }
        else{
            mSpinner.setSelection(mAdapter.getPosition(mCurrentSelection));
        }


        setVisibility(VISIBLE);
    }


    public int getSelection() {
        return mCurrentSelection;
    }


    private void getCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MembershipView, 0, 0);

        try {
            mHint = typedArray.getString(R.styleable.SpinnerWithHint_android_hint);
        } finally {
            typedArray.recycle();
        }
    }


    public boolean hasChanged() {
        return mCurrentSelection != mInitialSelection;
    }

    public boolean isEmpty(){ return mCurrentSelection == 0; }

}
