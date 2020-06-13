package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;
    private CrimeSuspect mSuspect;

    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public CrimeSuspect getSuspect() {
        return mSuspect;
    }

    public void setSuspect(CrimeSuspect suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public static class CrimeSuspect {

        private String mContactID; // Contacts Database ID
        private String mName;

        public CrimeSuspect(String contactID, String name){
            mContactID = contactID;
            mName = name;
        }

        public String getContactID() {
            return mContactID;
        }

        public void setContactID(String contactID) {
            mContactID = contactID;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

    }
}
