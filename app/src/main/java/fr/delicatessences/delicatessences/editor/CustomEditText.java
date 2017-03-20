package fr.delicatessences.delicatessences.editor;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;


public class CustomEditText extends LinearLayout {

    private static final int NONE = 1;
    private static final int CURRENCY = 2;
    private static final int VOLUME = 3;

    private EditText mEditText;
    private Button mButtonClear;
    private String mInitialText;
    private boolean mIsClearable;
    private TextView mUnitText;


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        getCustomAttributes(context, attrs);
        mInitialText = "";
    }


    private void initViews() {
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.custom_edit_text, this, true);
        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.setTextColor(getResources().getColor(R.color.primary_text_color));
        mEditText.setTextSize(16);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                if (mIsClearable && charSequence.length() > 0) {
                    mButtonClear.setVisibility(VISIBLE);
                } else {
                    mButtonClear.setVisibility(GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mUnitText = (TextView) findViewById(R.id.unit);

        mButtonClear = (Button) findViewById(R.id.buttonClear);
        mButtonClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setText("");
            }
        });


    }


    private void getCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0);

        try {
            mEditText.setInputType(typedArray.getInt(R.styleable.CustomEditText_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_NORMAL));
            mEditText.setHint(typedArray.getString(R.styleable.CustomEditText_android_hint));
            mIsClearable = typedArray.getBoolean(R.styleable.CustomEditText_clearEnabled, true);
            mButtonClear.setVisibility(mIsClearable ? INVISIBLE : GONE);
            int unit = typedArray.getInt(R.styleable.CustomEditText_unit, 1);

            Resources resources = getResources();
            switch (unit){
                case NONE:
                    mUnitText.setVisibility(GONE);
                    break;

                case CURRENCY:
                    mUnitText.setText(resources.getString(R.string.currency));
                    mUnitText.setVisibility(VISIBLE);
                    break;

                case VOLUME:
                    mUnitText.setText(resources.getString(R.string.volume_unit));
                    mUnitText.setVisibility(VISIBLE);
            }

        } finally {
            typedArray.recycle();
        }
    }


    public Editable getText() {
        return mEditText.getText();
    }

    public void setText(String text, boolean initial) {
        if (initial){
            mInitialText = text;
        }
        mEditText.setText(text);

    }



    public boolean hasChanged() {
        return !mInitialText.equals(mEditText.getText().toString());
    }
    public boolean isEmpty() {return mEditText.getText().toString().isEmpty();}

}

