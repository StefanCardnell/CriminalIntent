package com.bignerdranch.android.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.criminalintent.Crime;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSerious = getInt(getColumnIndex(CrimeTable.Cols.REQUIRES_POLICE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspectContactId = getString(getColumnIndex(CrimeTable.Cols.SUSPECT_CONTACT_ID));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setRequiresPolice(isSerious != 0);
        crime.setSolved(isSolved != 0);

        if(suspectContactId != null) {
            String suspectName = getString(getColumnIndex(CrimeTable.Cols.SUSPECT_NAME));
            Crime.CrimeSuspect suspect = new Crime.CrimeSuspect(suspectContactId, suspectName);
            crime.setSuspect(suspect);
        }

        return crime;

    }

}
