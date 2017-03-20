package fr.delicatessences.delicatessences.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;


public class CustomCheckBox extends CheckBox {

    private boolean mInitialValue;

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInitialValue = false;
    }


    public void setChecked(boolean checked, boolean initial){
        if (initial){
            mInitialValue = checked;
        }
        setChecked(checked);
    }

    public boolean hasChanged(){
        return mInitialValue != isChecked();
    }
}
