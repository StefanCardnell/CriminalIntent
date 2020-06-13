package com.bignerdranch.android.criminalintent;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class DayPickerActivity extends SingleFragmentActivity {

    public static final String EXTRA_DAY = "com.bidnerdranch.android.criminalintent.day";

    public static Intent newIntent(Context packageContext, Date date){
        Intent intent = new Intent(packageContext, DayPickerActivity.class);
        intent.putExtra(EXTRA_DAY, date);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DAY);
        return DayPickerFragment.newInstance(date);
    }

}
