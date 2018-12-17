package com.kobe.quickcall;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kobe.quickcall.adapter.MyAdapter;
import com.kobe.quickcall.greendao.ContactBean;
import com.kobe.quickcall.greendao.ContactBeanDao;
import com.kobe.quickcall.model.Contact;
import com.kobe.quickcall.utils.ContactUtil;
import com.kobe.quickcall.utils.L;

import java.security.Key;
import java.util.ArrayList;


public class MainActivity extends Activity implements TextWatcher, View.OnClickListener,
        MyAdapter.CallBack {

    private TextView mSearchView;
    private GridView mContactList;
    private MyAdapter mMyAdapter;
    private ImageView mIvUpdate;
    private ImageView mIvSettings;


    private boolean mLoadOK = false;
    private boolean lockLongPressKey;


    private ArrayList<Contact> mContactDataList;
    private ArrayList<ContactBean> mContactBeanList = new ArrayList<ContactBean>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mSearchView = (TextView) findViewById(R.id.search_view);
        mSearchView.addTextChangedListener(this);
        mContactList = (GridView) findViewById(R.id.contact_list);
        mIvUpdate = (ImageView) findViewById(R.id.iv_update);

        mIvSettings = (ImageView) findViewById(R.id.iv_settings);
        mIvSettings.setOnClickListener(this);
        mIvUpdate.setOnClickListener(this);
        requestPower();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        L.v(mSearchView.getText().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }

        if (event.getRepeatCount() == 0) {
            event.isTracking();
            lockLongPressKey = false;
        } else {
            lockLongPressKey = true;
        }

        if (!mLoadOK) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            if (lockLongPressKey && event.getRepeatCount() == 1) {
                mMyAdapter.makeCall(keyCode - 28);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            deleteKeyDown();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        lockLongPressKey = true;
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!mLoadOK) {
            return super.onKeyDown(keyCode, event);
        }

        L.v("keycode->" + keyCode);
        if ((keyCode == KeyEvent.KEYCODE_O || keyCode == KeyEvent.KEYCODE_I) && event
                .isAltPressed()) {
            mMyAdapter.changeFontSize(keyCode == KeyEvent.KEYCODE_O);
            return true;
        }
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            if (lockLongPressKey) {
                lockLongPressKey = false;
            } else {
                azKeyDown(keyCode);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            enterKeyDown();
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            spaceKeyDown();
        }
        return super.onKeyUp(keyCode, event);
    }

    //Delete Key
    private void deleteKeyDown() {
        L.v("deleteKeyDown");
        String oldStr = mSearchView.getText().toString();
        if (oldStr.length() >= 1) {
            mSearchView.setText(oldStr.substring(0, oldStr.length() - 1));
        }
        updateListView();
    }

    //A-Z  Key
    private void azKeyDown(int keyCode) {
        mSearchView.setText(mSearchView.getText().toString() + (char) (keyCode + 36));
        updateListView();
    }

    private void spaceKeyDown() {
        mSearchView.setText(mSearchView.getText().toString() + " ");
        updateListView();
    }

    //Enter Key
    private void enterKeyDown() {
        mMyAdapter.makeCall(0);
    }

    private void resetAfterMakeCall() {
        mSearchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchView.setText("");
                updateListView();
            }
        }, 1000);
    }

    //update search result list by text
    private void updateListView() {
        ArrayList<ContactBean> mContactBeanList = (ArrayList) MyApplication.getDaoInstant
                ().getContactBeanDao().queryBuilder().whereOr(ContactBeanDao.Properties
                .FirstPinName.like("%" + mSearchView.getText() + "%"), (ContactBeanDao.Properties
                .Name.like("%" + mSearchView.getText() + "%"))).limit(27).list();
        if (mMyAdapter == null) {
            mMyAdapter = new MyAdapter(mContactBeanList, this);
            mContactList.setAdapter(mMyAdapter);
            mContactList.setOnItemClickListener(mMyAdapter);
        } else {
            mMyAdapter.changeContactDataList(mContactBeanList);
        }
        mLoadOK = true;
    }

    //first time on create
    private void readContact(boolean override) {
        mSearchView.setText("");
        ArrayList<ContactBean> mContactBeanList = (ArrayList) MyApplication.getDaoInstant
                ().getContactBeanDao().queryBuilder().where(ContactBeanDao.Properties
                .FirstPinName.like("%" + mSearchView.getText() + "%")).limit(27).list();
        if (mContactBeanList.size() == 0 || override) {
            new ContactLoadTask().execute(this);//update local database
        } else {
            updateListView();
        }

    }

    private void requestPower() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission
                            .CALL_PHONE}, 1);

        } else {
            readContact(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        readContact(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_update) {
            readContact(true);
        } else if (v.getId() == R.id.iv_settings) {
        }
    }

    @Override
    public void makeCallFinish() {
        resetAfterMakeCall();
    }

    //load contact in backgroud
    class ContactLoadTask extends AsyncTask<Context, Integer, Boolean> {

        private Context mContext;

        @Override
        protected void onPreExecute() {
            mSearchView.setText(R.string.loading_contacts);
        }

        @Override
        protected Boolean doInBackground(Context... contexts) {
            mContext = contexts[0];
            try {
                mContactDataList = ContactUtil.loadContacts(contexts[0]);
                mContactBeanList.clear();
                for (Contact contact : mContactDataList) {
                    ContactBean contactBean = new ContactBean();
                    contactBean.setContactId(contact.getId());
                    contactBean.setName(contact.getName());
                    contactBean.setPinName(contact.getPinName());
                    contactBean.setFirstPinName(contact.getFirstPinName());
                    contactBean.setPhone(contact.getPhoneList().size() > 0 ? contact.getPhoneList
                            ().get(0) : "");
                    mContactBeanList.add(contactBean);
                }
                MyApplication.getDaoInstant().getContactBeanDao().deleteAll();
                MyApplication.getDaoInstant().getContactBeanDao().insertInTx(mContactBeanList);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            mLoadOK = result;
            if (result) {
                mSearchView.setText("");
                updateListView();
            } else {
                mSearchView.setText(R.string.load_fail);
            }
        }
    }
}
