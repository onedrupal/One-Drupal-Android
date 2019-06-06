package com.technikh.onedrupal.listviewitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.technikh.onedrupal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BarChartItem extends ChartItem {

    private final Typeface mTf;
    private String TAG = "BarChartItem";
    private String mChartHeading;

    public BarChartItem(ChartData<?> cd, Context c, String chartHeading) {
        super(cd);
        mChartHeading = chartHeading;
        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Light.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_BARCHART;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_barchart, null);
            holder.chart = convertView.findViewById(R.id.chart);
            holder.tvLabel = convertView.findViewById(R.id.tvChartLabel);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // MMdd 518.0 0518
                // date: last 2 characters
                Log.d(TAG, "onResponse: unixTime value "+value);
                long lValue = (long)value;
                Log.d(TAG, "onResponse: unixTime long value "+lValue);
                if(value > 0) {
                    Date nodeChangedDate = new Date(lValue* (24 * 60 * 60 * 1000));
                    SimpleDateFormat simpleDate =  new SimpleDateFormat("MMM d");
                    String xaxixLabel = simpleDate.format(nodeChangedDate);
                    return xaxixLabel;
                    /*
                    String valueStr = String.valueOf((int)value);
                    Log.d(TAG, "onResponse: unixTime valueStr " + valueStr);
                    String day = valueStr.substring(valueStr.length() - 2);
                    Log.d(TAG, "onResponse: unixTime day " + day);
                    String month = valueStr.substring(0, valueStr.length() - 2);
                    Log.d(TAG, "onResponse: unixTime month " + month);

                    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    return months[Integer.parseInt(month) - 1] + " " + day;*/
                }
                return String.valueOf(value);
            }
        });

        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(20f);
       // leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(20f);
       // rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = holder.chart.getLegend();
        //l.getEntries().
        //String x = holder.chart.get
        //String chartLabelTerm = holder.chart.getData().getDataSets().get(0).getLabel();
        holder.tvLabel.setText(mChartHeading);
        //l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
        //l.resetCustom();
        //l.setExtra(new int[] {Color.argb(50,104, 241, 175)}, new String[]{"Morning"});

        LegendEntry l1=new LegendEntry("Morning (5 am to 12 pm)", Legend.LegendForm.DEFAULT,10f,2f,null, Color.argb(50,255, 0, 0));
        LegendEntry l2=new LegendEntry("Afternoon (12 pm to 5 pm)", Legend.LegendForm.DEFAULT,10f,2f,null, Color.argb(50,0, 255, 0));
        LegendEntry l3=new LegendEntry("Evening/Night (5 pm to 4 am)", Legend.LegendForm.DEFAULT,10f,2f,null, Color.argb(50,0, 0, 255));
        //LegendEntry l4=new LegendEntry("Night (21 - 24)", Legend.LegendForm.DEFAULT,10f,2f,null, Color.argb(100,0, 0, 0));
        l.setCustom(new LegendEntry[]{l1,l2, l3});

       /* List<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
        LegendEntry legendEntry = new LegendEntry();
        legendEntry.formColor = Color.argb(50,104, 241, 175);
        legendEntry.label = "Morning";
        legendEntries.add(legendEntry);
        legendEntry.formColor = Color.argb(50,164, 228, 251);
        legendEntry.label = "Afternoon";
        legendEntries.add(legendEntry);
        //l.setCustom(new int[] {Color.argb(50,104, 241, 175)}, new String[]{"Morning"});
        l.setCustom(legendEntries);*/

        mChartData.setValueTypeface(mTf);

        // set data
        holder.chart.setData((BarData) mChartData);
        holder.chart.setFitBars(true);

        // do not forget to refresh the chart
//        holder.chart.invalidate();
        holder.chart.animateY(700);

        return convertView;
    }

    private static class ViewHolder {
        BarChart chart;
        TextView tvLabel;
    }
}
