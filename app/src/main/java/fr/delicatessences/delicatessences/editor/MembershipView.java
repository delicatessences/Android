package fr.delicatessences.delicatessences.editor;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import fr.delicatessences.delicatessences.R;


public class MembershipView extends LinearLayout
        implements OnClickListener, OnItemClickListener {


    private static final String NAME_COLUMN = "name";
    private static final String ID_COLUMN = "_id";
    private static final int COMMA_SEPARATED = 1;
    private static final int LIST = 2;


    public static final class MembershipSelectionItem {
        private final int mId;
        private final String mTitle;
        private boolean mChecked;

        public MembershipSelectionItem(int id, String title, boolean checked) {
            this.mId = id;
            this.mTitle = title;
            this.mChecked = checked;
        }

        public int getId() { return mId; }

       public String getTitle() {
            return mTitle;
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void setChecked(boolean checked) {
            mChecked = checked;
        }

        @Override
        public String toString() {
            return mTitle;
        }
    }


    private Cursor mCursor;
    private TextView mTextView;
    private ListPopupWindow mPopup;
    private ArrayList<Integer> mInitialMembers;
    private ArrayList<Integer> mCurrentMembers;
    private LayoutInflater mInflater;
    private Button mButtonPopup;
    private ArrayAdapter<MembershipSelectionItem> mAdapter;
    private int mDecoration;


    public MembershipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInitialMembers = new ArrayList<>();
        mCurrentMembers = mInitialMembers;
        updateView();
        getCustomAttributes(context, attrs);
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mTextView != null) {
            mTextView.setEnabled(enabled);
        }
    }



    public void setMetaData(Cursor metaData) {
        this.mCursor = metaData;
        updateView();
    }




    public void setMembers(ArrayList<Integer> ids, boolean initial) {
        mCurrentMembers = new ArrayList<>(ids);
        Collections.sort(mCurrentMembers);
        if (initial) {
            mInitialMembers = mCurrentMembers;
        }
        updateView();
    }



    private void updateView() {

        if (mInflater == null){
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInflater.inflate(R.layout.membership_view, this, true);
        }

        StringBuilder sb = new StringBuilder();
        if (mCursor != null) {
            mCursor.moveToPosition(-1);
            int idColumn = mCursor.getColumnIndex(ID_COLUMN);
            int nameColumn = mCursor.getColumnIndex(NAME_COLUMN);
            while (mCursor.moveToNext()) {
                int id = mCursor.getInt(idColumn);
                if (mCurrentMembers.contains(id)) {
                    String name = mCursor.getString(nameColumn);
                    if (!TextUtils.isEmpty(name)) {
                        switch (mDecoration){
                            case COMMA_SEPARATED:
                                if (sb.length() != 0) {
                                    sb.append(", ");
                                }

                                break;

                            case LIST:
                                if (sb.length() != 0) {
                                    sb.append("<br> ");
                                }
                                sb.append("&#8226; ");
                                break;
                        }
                        sb.append(name);
                    }
                }
            }
        }

        if (mTextView == null) {
            mTextView = (TextView) findViewById(R.id.members_text_view);
            mTextView.setOnClickListener(this);
            mTextView.setTextColor(getResources().getColor(R.color.secondary_text_color));
            mTextView.setTextSize(15);
        }

        mTextView.setEnabled(isEnabled());
        mTextView.setText(Html.fromHtml(sb.toString()));

        if (mButtonPopup == null){
            mButtonPopup = (Button) findViewById(R.id.button_popup);
            mButtonPopup.setOnClickListener(this);
        }

        setVisibility(VISIBLE);
    }




    private boolean closePopup(){
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (closePopup()) {
            mPopup = null;
            return;
        }

        mAdapter = new ArrayAdapter<>(
                getContext(), R.layout.membership_popup_item);

        mCursor.moveToPosition(-1);
        int nameColumn = mCursor.getColumnIndex(NAME_COLUMN);
        int idColumn = mCursor.getColumnIndex(ID_COLUMN);

        while (mCursor.moveToNext()) {
            String name = mCursor.getString(nameColumn);
            int id = mCursor.getInt(idColumn);
            boolean checked = hasMembership(id);
            mAdapter.add(new MembershipSelectionItem(id, name, checked));

        }

        mPopup = new ListPopupWindow(getContext(), null);
        mPopup.setAnchorView(mTextView);
        mPopup.setAdapter(mAdapter);
        mPopup.setModal(true);
        mPopup.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.show();

        ListView listView = mPopup.getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOverScrollMode(OVER_SCROLL_ALWAYS);
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            listView.setItemChecked(i, mAdapter.getItem(i).isChecked());
        }

        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closePopup();
        mPopup = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView list = (ListView) parent;
        int count = mAdapter.getCount();

        ArrayList<Integer> checked = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            MembershipSelectionItem item = mAdapter.getItem(i);
            item.setChecked(list.isItemChecked(i));
            if (item.isChecked()){
                checked.add(item.getId());
            }
        }

        Collections.sort(checked);
        mCurrentMembers = checked;

        updateView();
    }


    private boolean hasMembership(int id) {
        return mCurrentMembers.contains(id);
    }

    public ArrayList<Integer> getMembers(){
        return mCurrentMembers;
    }



    private void getCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MembershipView, 0, 0);

        try {
            mTextView.setHint(typedArray.getString(R.styleable.MembershipView_android_hint));
            mDecoration = typedArray.getInt(R.styleable.MembershipView_decoration, 1);
        } finally {
            typedArray.recycle();
        }
    }


    public boolean hasChanged() { return !mCurrentMembers.equals(mInitialMembers); }
    public boolean isEmpty(){ return mCurrentMembers.isEmpty(); }

    public ArrayList<Integer> getAdded(){
        ArrayList<Integer> added = new ArrayList<>(mCurrentMembers);
        added.removeAll(mInitialMembers);
        return added;
    }


    public ArrayList<Integer> getRemoved(){
        ArrayList<Integer> removed = new ArrayList<>(mInitialMembers);
        removed.removeAll(mCurrentMembers);
        return removed;
    }
}
