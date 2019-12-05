package com.example.android.inventoryapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;

public class Price implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private String current = "";

    public Price(EditText editText) {
        editTextWeakReference = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)){
            EditText editText = editTextWeakReference.get();
            editText.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("\\D", "");

            double parsed = 0.0;
            try {
                parsed = Double.parseDouble(cleanString);
            } catch (Exception e) {
                System.out.println("String was empty");
            }
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));
            current = formatted;
            editText.setText(formatted);
            editText.setSelection(formatted.length()-2);
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
