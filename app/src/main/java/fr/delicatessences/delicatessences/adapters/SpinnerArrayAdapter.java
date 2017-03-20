package fr.delicatessences.delicatessences.adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import fr.delicatessences.delicatessences.R;


public class SpinnerArrayAdapter extends ArrayAdapter<SpinnerArrayAdapter.SpinnerItem>{


    public static final class SpinnerItem {
        private final int mId;
        private final String mTitle;

        public SpinnerItem(int id, String title) {
            this.mId = id;
            this.mTitle = title;
        }

        public int getId() {
            return mId;
        }

        public String getTitle() {
            return mTitle;
        }




        @Override
        public String toString() {
            return mTitle;
        }
    }


    public SpinnerArrayAdapter(Context context) {
        super(context, R.layout.spinner_item);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (position == getCount()) {
            TextView textView = (TextView) v.findViewById(R.id.item_label);
            textView.setText("");
            textView.setHint(getItem(getCount()).getTitle());
        }

        return v;
    }

    @Override
    public long getItemId(int position) {
        SpinnerItem item = getItem(position);
        return item.getId();
    }

    public int getPosition(int id){
        int count = getCount();
        for (int i = 0; i < count; i++){
            if (getItemId(i) == id){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }
}
