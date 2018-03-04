package br.senai.sp.informatica.mobile.bikemobi.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Calendar;


/**
 * Created by 34023325821 on 19/01/2018.
 */

public class DateDialog extends DialogFragment {

    private Calendar calendar;
    private EditText editText;
    private View view;


    private static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                editText.setText(fmt.format(calendar.getTime()));
            }
        };
        if (calendar == null){
            calendar = Calendar.getInstance();
        }
        DatePickerDialog dialog = new DatePickerDialog(view.getContext(),
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return dialog;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public void setView(View view) {
        this.view = view;
    }
}
