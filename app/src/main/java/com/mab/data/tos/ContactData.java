package com.mab.data.tos;

import java.io.Serializable;

/**
 * Created by Jaideep.Lakshminaray on 09-03-2017.
 */

public class ContactData implements Serializable {
    private long id;
    private String name;
    private String number;
    private String emailid;
    private String photoUri;

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }
}
