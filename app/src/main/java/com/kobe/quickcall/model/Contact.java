package com.kobe.quickcall.model;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;

public class Contact {

    private String id;
    private String name;
    private String pinName = "@_";
    private String firstPinName;
    private ArrayList<String> phoneList = new ArrayList<>();

    public Contact() {

    }

    public Contact(String id, String name, ArrayList<String> phone) {
        this.id = id;
        this.name = name;
        this.phoneList = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPhoneList() {
        return phoneList;
    }

    public String getPinName() {
        return pinName;
    }

    public String getFirstPinName() {
        return firstPinName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        if (name != null) {
            char[] nameChars = name.toCharArray();
            for (int i = 0; i < nameChars.length; i++) {
                if (Pinyin.isChinese(nameChars[i]))
                    pinName += Pinyin.toPinyin(nameChars[i]) + ((i == nameChars.length - 1) ? ""
                            : "_");
            }
            setFirstPinName(pinName);
        }

    }

    public void setPhoneList(ArrayList<String> phoneList) {
        this.phoneList = phoneList;
    }

    public void addPhone(String phone) {
        phoneList.add(phone);
    }

    private void setFirstPinName(String pinName) {
        String[] pins = pinName.split("_");
        StringBuffer sb = new StringBuffer();
        for (String pin : pins) {
            if (pin.length() > 0) sb.append(pin.charAt(0));
        }
        firstPinName = sb.toString();
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pinName='" + pinName + '\'' +
                ", firstPinName='" + firstPinName + '\'' +
                ", phoneList=" + phoneList +
                '}';
    }
}
