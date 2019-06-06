package com.technikh.onedrupal.custom;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter extends ValueFormatter
{

    private final BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float timeStamp) {
        if(false)
            return timeStamp+" hello";
        Date time=new Date((long)timeStamp*1000);

        if (chart.getVisibleXRange() > 30 * 6) {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd HH:mm");
            return simpleDate.format(time);
        } else {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("HH:mm");
            return simpleDate.format(time);
        }
    }
}
