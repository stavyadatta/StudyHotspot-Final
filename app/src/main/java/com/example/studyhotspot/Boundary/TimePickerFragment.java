package com.example.studyhotspot.Boundary;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * TimePickerFragment is designed with Human Computer Interaction in mind.
 * Instead of having to input the start time & end time whenever they create a session, which may introduce
 * errors, TimePickerFragment allows the users to choose the timing from a clock.
 */
public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SimpleDateFormat sdfHour =new SimpleDateFormat("HH");
        SimpleDateFormat sdfMin =new SimpleDateFormat("mm");
        TimeZone tz = TimeZone.getTimeZone("Asia/Singapore");

        sdfHour.setTimeZone(tz);
        sdfMin.setTimeZone(tz);

        Date time = Calendar.getInstance().getTime();
        int hour = Integer.parseInt(sdfHour.format(time));
        int minute = Integer.parseInt(sdfMin.format(time));

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}