package fr.delicatessences.delicatessences.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.delicatessences.delicatessences.R;

public class NavigationDrawerArrayAdapter extends ArrayAdapter<NavigationDrawerArrayAdapter.NavigationRowItem> {


    public static class NavigationRowItem {

        private final String title;
        private final int imageId;

        public NavigationRowItem(String title, int imageId) {
            this.title = title;
            this.imageId = imageId;
        }

        public String getTitle() {
            return title;
        }

        public int getImageId() {
            return imageId;
        }

    }


    private final Context context;

    public NavigationDrawerArrayAdapter(Context context, List<NavigationRowItem> items) {
        super(context, R.layout.navigation_drawer_row_item, items);
        this.context = context;
    }


    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        NavigationRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.navigation_drawer_row_item, parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.item_label);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.getTitle());
        Resources resources = context.getResources();
        holder.txtTitle.setTextColor(resources.getColor(R.color.navigation_drawer_text_color));

        holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, rowItem.getImageId()));

        return convertView;
    }

}
