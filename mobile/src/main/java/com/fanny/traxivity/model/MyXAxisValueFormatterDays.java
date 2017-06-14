package com.fanny.traxivity.model;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
/**
 * Created by jbjourget.
 */
public class MyXAxisValueFormatterDays implements IAxisValueFormatter {
    private String[] mValues;
    public MyXAxisValueFormatterDays(String[] values) {
        this.mValues = values;
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }
}