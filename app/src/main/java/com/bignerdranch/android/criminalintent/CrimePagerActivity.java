package com.bignerdranch.android.criminalintent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private Button mFirstButton;
    private Button mLastButton;

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager = findViewById(R.id.crime_view_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Nothing to do
            }

            @Override
            public void onPageSelected(int position) {
                mFirstButton.setEnabled(position != 0);
                mLastButton.setEnabled(position != (mCrimes.size()-1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Nothing to do
            }
        });

        mFirstButton = findViewById(R.id.first_button);
        mFirstButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mLastButton = findViewById(R.id.last_button);
        mLastButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size()-1);
            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for(int i = 0; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }





    }
}
