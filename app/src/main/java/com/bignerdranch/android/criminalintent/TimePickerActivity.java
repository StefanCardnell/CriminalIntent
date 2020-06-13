package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {

    public static final String EXTRA_TIME = "com.bidnerdranch.android.criminalintent.day";

    public static Intent newIntent(Context packageContext, Date date){
        Intent intent = new Intent(packageContext, TimePickerActivity.class);
        intent.putExtra(EXTRA_TIME, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_TIME);
        return TimePickerFragment.newInstance(date);
    }

}
