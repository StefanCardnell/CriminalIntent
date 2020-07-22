package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DayPickerFragment extends DialogFragment {

    public static final String EXTRA_DAY = "com.bidnerdranch.android.criminalintent.day";

    private static final String ARG_DAY = "day";

    private DatePicker mDatePicker;
    private Button mConfirmButton;

    private Calendar mCalendar;

    public static DayPickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY, date);

        DayPickerFragment fragment = new DayPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_DAY);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mConfirmButton = v.findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.set(Calendar.YEAR, mDatePicker.getYear());
                mCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
                mCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                Date date = mCalendar.getTime();
                sendResult(Activity.RESULT_OK, date);
            }
        });

        return v;

    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//
//        Date date = (Date) getArguments().getSerializable(ARG_DAY);
//
//        mCalendar = Calendar.getInstance();
//        mCalendar.setTime(date);
//
//        int year = mCalendar.get(Calendar.YEAR);
//        int month = mCalendar.get(Calendar.MONTH);
//        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
//
//        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
//
//        mDatePicker = v.findViewById(R.id.dialog_date_picker);
//        mDatePicker.init(year, month, day, null);
//
//        return new AlertDialog.Builder(getActivity())
//            .setView(v)
//            .setTitle(R.string.date_picker_title)
//            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mCalendar.set(Calendar.YEAR, mDatePicker.getYear());
//                    mCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
//                    mCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
//                    Date date = mCalendar.getTime();
//                    sendResult(Activity.RESULT_OK, date);
//                }
//            })
//            .create();
//    }

    protected void sendResult(int resultCode, Date date){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DAY, date);
        if(getTargetFragment() == null){
            getActivity().setResult(resultCode, intent);
            getActivity().finish();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
