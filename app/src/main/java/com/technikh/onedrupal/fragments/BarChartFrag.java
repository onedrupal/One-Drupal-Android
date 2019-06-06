package com.technikh.onedrupal.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.client.utils.DateUtils;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import treeutil.MyObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.custom.DayAxisValueFormatter;
import com.technikh.onedrupal.custom.MyMarkerView;
import com.technikh.onedrupal.listviewitems.BarChartItem;
import com.technikh.onedrupal.listviewitems.ChartItem;
import com.technikh.onedrupal.models.ModelFanPosts;
import com.technikh.onedrupal.models.NodeSimpleFields;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.network.AddCookiesInterceptor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class BarChartFrag extends SimpleFragment {
    //public class BarChartFrag extends SimpleFragment implements OnChartGestureListener {

    @NonNull
    public static Fragment newInstance(int i, String protocol, String domain) {
        Bundle args = new Bundle();
        args.putInt("tab", i);
        args.putString("SiteProtocol", protocol);
        args.putString("SiteDomain", domain);
        BarChartFrag fragment = new BarChartFrag();
        fragment.setArguments(args);
        return fragment;
        //return new BarChartFrag();
    }

    //private CombinedChart chart;
    //private LineChart legendChart;
    private ListView lv;
    private String mSiteDomain, mSiteProtocol;
    private int dataSetsCount = 3;
    private ChartDataAdapter cda;
    ArrayList<ChartItem> chartAdapterDatalist = new ArrayList<>();
    int tab = 0;
    private String TAG = "BarChartFrag";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_simple_bar, container, false);

        if (getArguments() != null) {
            tab = getArguments().getInt("tab");
            mSiteProtocol = getArguments().getString("SiteProtocol");
            mSiteDomain = getArguments().getString("SiteDomain");
        }
        lv = v.findViewById(R.id.listView1);
        cda = new ChartDataAdapter(context, chartAdapterDatalist);
        lv.setAdapter(cda);

        getDataFromAPI();
/*
        chart = v.findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        //xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // MMdd 518.0 0518
                // date: last 2 characters
                Log.d(TAG, "onResponse: unixTime value "+value);
                if(value > 0) {
                    String valueStr = String.valueOf(value);
                    Log.d(TAG, "onResponse: unixTime valueStr " + valueStr);
                    String day = valueStr.substring(valueStr.length() - 4, valueStr.length() - 2);
                    Log.d(TAG, "onResponse: unixTime day " + day);
                    String month = valueStr.substring(0, valueStr.length() - 4);
                    Log.d(TAG, "onResponse: unixTime month " + month);

                    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                    return months[Integer.parseInt(month) - 1] + " " + day;
                }
                return String.valueOf(value);
            }
        });*/

/*

        // create a new chart object
        chart = new BarChart(getActivity());
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(true);
        chart.setOnChartGestureListener(this);
        chart.setHighlightFullBarEnabled(true);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(true);

        chart.setDrawGridBackground(true);
        getDataFromAPI();

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);

        //chart.setDrawGridBackground(false);
        //chart.setDrawBarShadow(false);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        //chart.setData(generateBarData(1, 20000, 12));

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        //xAxis.setTypeface(tf);
        //xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        //xAxis.setGranularityEnabled(true);
        //chart.getViewPortHandler().setMaximumScaleX(5f);
        //chart.getViewPortHandler().setMaximumScaleY(5f);
        //xAxis.setCenterAxisLabels(true);
        //ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        //xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tf);
        //leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        // programmatically add the chart
        FrameLayout parent = v.findViewById(R.id.parentLayout);
        parent.addView(chart);
*/
        //getDataFromAPI();
        return v;
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        public void setData(List<ChartItem> objects) {

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private void getDataFromAPI(){
        SettingsType nTypeObj = MyApplication.gblGetNodeTypeFromPosition(tab-2);
        if(nTypeObj == null){
            return;
        }
        // http://one-drupal-demo2.technikh.com/onedrupal/api/v1/content-fields/health_tracker?items_per_page=500
        String newUrl = mSiteProtocol+mSiteDomain + "/onedrupal/api/v1/content-fields/"+nTypeObj.getNodeType()+"?items_per_page=500";
        //String newUrl = "http://one-drupal-demo2.technikh.com/onedrupal/api/v1/content/health_tracker";
        File httpCacheDirectory = new File(getActivity().getCacheDir(), "offlineCache");
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //10 MB
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new AddCookiesInterceptor())
                .build();
        Request request;
        try{
            request = new Request.Builder().url(newUrl).build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String respoStr = response.body().string();
                if(response.isSuccessful()){
                    try{
                        JSONObject result = new JSONObject(respoStr);
                        JSONArray ja = result.getJSONArray("results");
                        /*List<BarEntry> entries = new ArrayList<BarEntry>();
                        List<BarEntry> entries1 = new ArrayList<BarEntry>();
                        ArrayList<Integer> colors_entries1 = new ArrayList<Integer>();

                        List<BarEntry> entries2 = new ArrayList<BarEntry>();
                        List<BarEntry> entries3 = new ArrayList<BarEntry>();
                        List<BarEntry> entries4 = new ArrayList<BarEntry>();

                        List<Entry> lineentries1 = new ArrayList<>();
                        List<Entry> lineentries2 = new ArrayList<>();
                        List<Entry> lineentries3 = new ArrayList<>();
                        List<Entry> lineentries4 = new ArrayList<>();

                        List<BubbleEntry> bubbleentries1 = new ArrayList<>();
                        List<BubbleEntry> bubbleentries2 = new ArrayList<>();
                        List<BubbleEntry> bubbleentries3 = new ArrayList<>();
                        List<BubbleEntry> bubbleentries4 = new ArrayList<>();

                        HashMap<String, String> termChartMap = new HashMap<>();*/
                        HashMap<String, ArrayList<BarEntry>> termChartDataMap = new HashMap<>();
                        HashMap<String, ArrayList<Integer>> termChartColorDataMap = new HashMap<>();
                        //ArrayList<BarEntry> listviewBarEntries = new ArrayList<>();
                        //String[] chartTypesArray = {"line", "bar", "bubble"};
                        //int chartTypeCtr = 0;
                        for (int j = 0; j < ja.length(); j++) {
                            JSONObject jo = (JSONObject) ja.get(j);
                            NodeSimpleFields modelFanPosts = new NodeSimpleFields(jo);
                            // 2019-05-13T13:15:01+00:00
                            // http://sdfonlinetester.info/
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                            Log.d(TAG, "onResponse: modelFanPosts.node_changed_date_str " + modelFanPosts.changed);
                            try {
                                //Date nodeChangedDate = dateFormat.parse(modelFanPosts.changed);
                                Date nodeChangedDate = new Date(modelFanPosts.changed*1000);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(nodeChangedDate);
                                int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);
                                Log.d(TAG, "onResponse: timeOfDay" + timeOfDay);

                                // https://developer.android.com/reference/java/text/SimpleDateFormat
                                SimpleDateFormat simpleDate =  new SimpleDateFormat("MMdd");
                                String xaxixLabel = simpleDate.format(nodeChangedDate);
                                //int unixTime = Integer.valueOf(xaxixLabel);

                                //long unixTime = modelFanPosts.changed;
                                //Log.d(TAG, "onResponse: unixTime "+unixTime);
                                //Date date = new Date();
                                //Calendar cal = Calendar.getInstance();
                                //cal.setTime(date);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                cal.add(Calendar.DATE, 1);
                                Date noTimeDate = cal.getTime();
                                long unixTime = noTimeDate.getTime() / (24 * 60 * 60 * 1000);
                                Log.d(TAG, "onResponse: unixTime modelFanPosts.changed "+modelFanPosts.changed);
                                Log.d(TAG, "onResponse: unixTime noTimeDate "+unixTime);

                                String nodeTag = modelFanPosts.term;
                                Log.d(TAG, "onResponse: nodeTag "+nodeTag);
                                if(termChartDataMap.get(nodeTag) == null){
                                    termChartDataMap.put(nodeTag, new ArrayList<BarEntry>());
                                    termChartColorDataMap.put(nodeTag, new ArrayList<Integer>());
                                }
                                termChartDataMap.get(nodeTag).add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.title)));
                                if (timeOfDay >= 5 && timeOfDay < 12) {
                                    termChartColorDataMap.get(nodeTag).add(Color.argb(50,255, 0, 0));
                                } else if (timeOfDay >= 12 && timeOfDay < 17) {
                                    termChartColorDataMap.get(nodeTag).add(Color.argb(50,0, 255, 0));
                              //  } else if (timeOfDay >= 16 && timeOfDay < 21) {
                                    //termChartColorDataMap.get(nodeTag).add(Color.argb(100,0, 0, 255));
                                } else{
                                    termChartColorDataMap.get(nodeTag).add(Color.argb(50,0, 0, 255));
                                }
                                /*
                                if(termChartMap.get(nodeTag) == null){
                                    Log.d(TAG, "onResponse: chartTypesArray[chartTypeCtr] "+chartTypesArray[chartTypeCtr]);
                                    termChartMap.put(nodeTag, chartTypesArray[chartTypeCtr]);
                                    chartTypeCtr++;
                                    if(chartTypeCtr >= 3){
                                        chartTypeCtr = 0;
                                    }
                                }
                                //float unixTime = j%2;
                                if(termChartMap.get(nodeTag).equals("line1")){
                                    Log.d(TAG, "onResponse: equals line");
                                    if (timeOfDay >= 0 && timeOfDay < 12) {
                                        lineentries1.add(new Entry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 12 && timeOfDay < 16) {
                                        lineentries2.add(new Entry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 16 && timeOfDay < 21) {
                                        lineentries3.add(new Entry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 21 && timeOfDay < 24) {
                                        lineentries4.add(new Entry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    }
                                }else if(termChartMap.get(nodeTag).equals("bar")){
                                    Log.d(TAG, "onResponse: equals bar timeOfDay"+timeOfDay+" modelFanPosts.getTitle() "+modelFanPosts.getTitle());
                                    entries1.add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    if (timeOfDay >= 0 && timeOfDay < 12) {
                                        colors_entries1.add(Color.argb(50,104, 241, 175));
                                        //colors_entries1.add(Color.rgb(104, 241, 175));
                                        //entries1.add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 12 && timeOfDay < 16) {
                                        colors_entries1.add(Color.argb(50,164, 228, 251));
                                        //entries2.add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 16 && timeOfDay < 21) {
                                        colors_entries1.add(Color.argb(50,242, 247, 158));
                                        //entries3.add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    } else if (timeOfDay >= 21 && timeOfDay < 24) {
                                        colors_entries1.add(Color.argb(50,255, 102, 0));
                                        //entries4.add(new BarEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle())));
                                    }
                                }else if(termChartMap.get(nodeTag).equals("bubble1")){
                                    Log.d(TAG, "onResponse: equals bubble unixTime "+unixTime+" timeOfDay"+timeOfDay+" modelFanPosts.getTitle() "+modelFanPosts.getTitle());
                                    bubbleentries1.add(new BubbleEntry(unixTime, 140, 2));

                                    if (timeOfDay >= 0 && timeOfDay < 12) {
                                        bubbleentries1.add(new BubbleEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle()), 2));
                                    } else if (timeOfDay >= 12 && timeOfDay < 16) {
                                        bubbleentries1.add(new BubbleEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle()), 2));
                                    } else if (timeOfDay >= 16 && timeOfDay < 21) {
                                        Log.d(TAG, "onResponse: equals bubble timeOfDay >= 16 && timeOfDay < 21 unixTime "+unixTime+" timeOfDay"+timeOfDay+" modelFanPosts.getTitle() "+modelFanPosts.getTitle());
                                        //bubbleentries1.add(new BubbleEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle()), 2));
                                    } else if (timeOfDay >= 21 && timeOfDay < 24) {
                                        bubbleentries1.add(new BubbleEntry(unixTime, Float.parseFloat(modelFanPosts.getTitle()), 2));
                                    }
                                }*/

                                /*entries.add(new BarEntry(
                                        j,
                                        new float[]{val1, val2, val3}));*/
                            }catch (Exception e){
                            //}catch (ParseException e){
                                e.printStackTrace();
                            }
                        }

                        Set set = termChartDataMap.entrySet();
                        Iterator iterator = set.iterator();

                        while(iterator.hasNext()) {
                            Map.Entry mentry = (Map.Entry) iterator.next();
                            ArrayList<BarEntry> subEntries = (ArrayList<BarEntry>) mentry.getValue();

                            BarDataSet d = new BarDataSet(subEntries, "Term: " + mentry.getKey().toString());
                            d.setColors(termChartColorDataMap.get(mentry.getKey()));
                            //d.setHighLightAlpha(255);

                            BarData cd = new BarData(d);
                            cd.setBarWidth(0.9f);
                            chartAdapterDatalist.add(new BarChartItem(cd, context, mentry.getKey().toString()));
                        }

/*
                        for (int i = 0; i < 3; i++) {
                            list.add(new BarChartItem(generateDataBar(i + 1), context));
                        }*/
                        //ChartDataAdapter cda = new ChartDataAdapter(context, list);
                        //cda.setData(list);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cda.notifyDataSetChanged();
                            }
                        });
                        //cda.notifyDataSetChanged();
                        //lv.setAdapter(cda);
/*
                        BarDataSet dataSet1 = new BarDataSet(entries1, "Morning (0 - 12)"); // add entries to dataset
                        //int[] colors_entries1 = new int[3];
                        //colors_entries1[0] = Color.rgb(104, 241, 175);
                        dataSet1.setColors(colors_entries1);
                        dataSet1.setColor(Color.rgb(104, 241, 175));
                        BarDataSet dataSet2 = new BarDataSet(entries2, "Afternoon (12 - 16)");
                        dataSet2.setColor(Color.rgb(164, 228, 251));
                        BarDataSet dataSet3 = new BarDataSet(entries3, "Evening (16 - 21)");
                        dataSet3.setColor(Color.rgb(242, 247, 158));
                        BarDataSet dataSet4 = new BarDataSet(entries4, "Night (21 - 24)");
                        dataSet4.setColor(Color.rgb(255, 102, 0));

                        LineDataSet linedataSet1 = new LineDataSet(lineentries1, "Morning (0 - 12)"); // add entries to dataset
                        linedataSet1.setColor(Color.rgb(104, 241, 175));
                        linedataSet1.setDrawValues(true);
                        LineDataSet linedataSet2 = new LineDataSet(lineentries2, "Afternoon (12 - 16)");
                        linedataSet2.setColor(Color.rgb(164, 228, 251));
                        LineDataSet linedataSet3 = new LineDataSet(lineentries3, "Evening (16 - 21)");
                        linedataSet3.setColor(Color.rgb(242, 247, 158));
                        LineDataSet linedataSet4 = new LineDataSet(lineentries4, "Night (21 - 24)");
                        linedataSet4.setColor(Color.rgb(255, 102, 0));

                        BubbleDataSet bubbleDataSet1 = new BubbleDataSet(bubbleentries1, "Morning (0 - 12)"); // add entries to dataset
                        bubbleDataSet1.setColor(Color.argb(50, 104, 241, 175));
                        BubbleDataSet bubbleDataSet2 = new BubbleDataSet(bubbleentries2, "Afternoon (12 - 16)");
                        bubbleDataSet2.setColor(Color.argb(50,164, 228, 251));
                        BubbleDataSet bubbleDataSet3 = new BubbleDataSet(bubbleentries3, "Evening (16 - 21)");
                        bubbleDataSet3.setColor(Color.argb(50,242, 247, 158));
                        BubbleDataSet bubbleDataSet4 = new BubbleDataSet(bubbleentries4, "Night (21 - 24)");
                        bubbleDataSet4.setColor(Color.argb(50,255, 102, 0));

                        //BarData barData = new BarData(dataSet1, dataSet2, dataSet3, dataSet4);
                        BarData barData = new BarData(dataSet1);

                        LineData lineData = new LineData(linedataSet1, linedataSet2, linedataSet3, linedataSet4);

                        BubbleData bubbleData = new BubbleData(bubbleDataSet1, bubbleDataSet2, bubbleDataSet3, bubbleDataSet4);

                        barData.setValueFormatter(new LargeValueFormatter());
                        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
                        barData.setValueTypeface(tf);
                        barData.setHighlightEnabled(true);
                        //chart.setData(lineData);
                        //chart.invalidate(); // refresh

                        CombinedData combinedData = new CombinedData();
                        combinedData.setData(barData);
                        combinedData.setData(lineData);
                        combinedData.setData(bubbleData);
                        chart.setData(combinedData);
                        chart.invalidate(); // refresh
                        */
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }

/*
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        chart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart long pressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart fling. VelocityX: " + velocityX + ", VelocityY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
		Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
	}
*/
}
