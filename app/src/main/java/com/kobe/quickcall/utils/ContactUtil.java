package com.kobe.quickcall.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.kobe.quickcall.model.Contact;

import java.util.ArrayList;


public class ContactUtil {


    public static ArrayList<Contact> loadContacts(Context context) throws Exception {

        Uri uri = Uri.parse("content://com.android.contacts/contacts");

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null,
                null);
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        while (cursor.moveToNext()) {

            Contact contact = new Contact();
            int contractID = cursor.getInt(0);

            StringBuilder sb = new StringBuilder("contractID=");

            contact.setId(String.valueOf(contractID));
            sb.append(contractID);

            uri = Uri.parse("content://com.android.contacts/contacts/"
                    + contractID + "/data");

            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype",
                    "data1", "data2"}, null, null, null);

            while (cursor1.moveToNext()) {

                String data1 = cursor1.getString(cursor1
                        .getColumnIndex("data1"));

                String mimeType = cursor1.getString(cursor1
                        .getColumnIndex("mimetype"));

                if ("vnd.android.cursor.item/name".equals(mimeType)) { // 是姓名
                    contact.setName(data1);
                    sb.append(",name=" + data1);

                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { // 手机
                    contact.addPhone(data1.replaceAll("-", ""));
                    sb.append(",phone=" + data1);
                }

            }
            contacts.add(contact);
            cursor1.close();

            L.i(contact.toString());

        }

        cursor.close();
        return contacts;

    }


}
