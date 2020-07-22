package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "com.bidnerdranch.android.criminalintent.time";

    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;
    private Button mConfirmButton;

    private Calendar mCalendar;

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);

        mConfirmButton = v.findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCalendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
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
//        Date date = (Date) getArguments().getSerializable(ARG_TIME);
//
//        mCalendar = Calendar.getInstance();
//        mCalendar.setTime(date);
//
//        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
//        int minute = mCalendar.get(Calendar.MINUTE);
//
//        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
//
//        mTimePicker = v.findViewById(R.id.dialog_time_picker);
//        mTimePicker.setCurrentHour(hour);
//        mTimePicker.setCurrentMinute(minute);
//
//        return new AlertDialog.Builder(getActivity())
//            .setView(v)
//            .setTitle(R.string.time_picker_title)
//            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mCalendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
//                    mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
//                    Date date = mCalendar.getTime();
//                    sendResult(Activity.RESULT_OK, date);
//                }
//            })
//            .create();
//    }

    protected void sendResult(int resultCode, Date date){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        if(getTargetFragment() == null){
            getActivity().setResult(resultCode, intent);
            getActivity().finish();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
