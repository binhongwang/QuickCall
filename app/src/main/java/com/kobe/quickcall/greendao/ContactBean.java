package com.kobe.quickcall.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ContactBean {

    @Id(autoincrement = true)
    private Long id;

    private String contactId;
    private String name;
    private String pinName = "@_";
    private String firstPinName;
    private String phone;
    @Generated(hash = 1678684618)
    public ContactBean(Long id, String contactId, String name, String pinName,
            String firstPinName, String phone) {
        this.id = id;
        this.contactId = contactId;
        this.name = name;
        this.pinName = pinName;
        this.firstPinName = firstPinName;
        this.phone = phone;
    }
    @Generated(hash = 1283900925)
    public ContactBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContactId() {
        return this.contactId;
    }
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPinName() {
        return this.pinName;
    }
    public void setPinName(String pinName) {
        this.pinName = pinName;
    }
    public String getFirstPinName() {
        return this.firstPinName;
    }
    public void setFirstPinName(String firstPinName) {
        this.firstPinName = firstPinName;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }


}
