package com.fanny.traxivity;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class CustomMarkerView extends MarkerView {
    /**
     * The TextView where the content is displayed
     */
    private TextView tvContent;


    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)

    /**
     * This method gets called everytime the MarkerView is redrawn, and gives you the chance to update the content it displays (e.g. set the text for a TextView, ...).
     * @param e currently highlighted Entry
     * @param highlight corresponding Highlight object
     */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry bE = (BarEntry) e;
        float val = bE.getYVals()[highlight.getStackIndex()]*6;
        String text;
        if (val != 0) {
            System.out.println("val = " + val);
            int min = (int) (val % 360) / 6;
            int sec = (int) (val % 360) % 6;
            text = min + "'"+sec+"0''" ;
        }else{
            text = "";
        }


        tvContent.setText(text); // set the entry-value as the display text
    }

    /**
     * Return the offset to the position on the x-axis where the marker is drawn. By default, the marker will be drawn with it's top left edge at the position of the entry.
     * @param xpos represent the default drawing position of the marker.
     * @return offset to the position on the x-axis where the marker is drawn.
     */
    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    /**
     * Return the offset to the position on the y-axis where the marker is drawn. By default, the marker will be drawn with it's top left edge at the position of the entry.
     * @param ypos represent the default drawing position of the marker
     * @return offset to the position on the y-axis where the marker is drawn
     */
    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
