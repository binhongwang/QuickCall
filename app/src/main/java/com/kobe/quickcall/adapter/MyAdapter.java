package com.kobe.quickcall.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kobe.quickcall.R;
import com.kobe.quickcall.greendao.ContactBean;
import com.kobe.quickcall.utils.DisplayUtil;
import com.kobe.quickcall.utils.L;
import com.kobe.quickcall.utils.SpUtil;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private Context mContext = null;
    private ArrayList<ContactBean> mContactDataList;
    private LayoutInflater inflater = null;

    private int[] fontSize = {10, 12, 14, 16, 18, 20};
    private int fontSizeIndex = 5;

    public MyAdapter(ArrayList<ContactBean> contactDataList, Context context) {
        this.mContactDataList = contactDataList;
        this.mContext = context;
        this.fontSizeIndex = (int) SpUtil.getParam(mContext, "font_size", 5);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mContactDataList.size();
    }

    @Override
    public Object getItem(int position) {
        mContactDataList.get(0);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactBean contact = mContactDataList.get(position);
        View view = inflater.inflate(R.layout.listview_item, null);
        TextView txtIndex = (TextView) view.findViewById(R.id.txt_index);
        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        ImageView ivEnter = (ImageView) view.findViewById(R.id.iv_enter);
        if (position == 0) {
            txtIndex.setVisibility(View.GONE);
            ivEnter.setVisibility(View.VISIBLE);
        } else {
            ivEnter.setVisibility(View.GONE);
            txtIndex.setText(((char) (position + 64)) + ".");
        }
        txtName.setText(contact.getName());
        txtName.setTextSize(DisplayUtil.px2sp(mContext, DisplayUtil.dip2px(mContext,
                fontSize[fontSizeIndex])));
        return view;
    }

    public void changeContactDataList(ArrayList<ContactBean> contactDataList) {
        this.mContactDataList = contactDataList;
        this.notifyDataSetInvalidated();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        makeCall(position);
    }


    //make a call by position
    public void makeCall(int position) {
        if (position > mContactDataList.size()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mContactDataList.get(position).getPhone()));
        L.v("tel:" + mContactDataList.get(position).getPhone());
        mContext.startActivity(intent);
        ((CallBack) mContext).makeCallFinish();
    }

    public interface CallBack {
        void makeCallFinish();
    }

    public void changeFontSize(boolean bigger) {
        int size = (int) SpUtil.getParam(mContext, "font_size", 5);
        if (bigger) {
            SpUtil.setParam(mContext, "font_size", size < 5 ? ++size : size);
        } else {
            SpUtil.setParam(mContext, "font_size", size > 0 ? --size : size);
        }
        fontSizeIndex = (int) SpUtil.getParam(mContext, "font_size", 5);
        this.notifyDataSetInvalidated();
    }
}
