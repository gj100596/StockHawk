package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends Activity {

    private static final int DAY = 1;
    private static final int WEEK = 2;
    private static final int MONTH = 3;
    private static final int YEAR = 4;
    private static final int FIVE_YEAR = 5;
    private static final int MAX = 6;


    String symbol, startDate, endDate;
    private OkHttpClient client = new OkHttpClient();
    ArrayList<JSONObject> quote;
    LineChart chart;
    TextView day,week,month,year,fiveYear,max,symbolTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        chart = (LineChart) findViewById(R.id.linechart);
        day = (TextView) findViewById(R.id.dayInDetail);
        week = (TextView) findViewById(R.id.weekInDetail);
        month = (TextView) findViewById(R.id.monthInDetail);
        year = (TextView) findViewById(R.id.yearInDetail);
        fiveYear = (TextView) findViewById(R.id.fiveYearInDetail);
        max = (TextView) findViewById(R.id.maxInDetail);
        symbolTextView = (TextView) findViewById(R.id.symbolInDetail);

        Bundle arg = getIntent().getExtras();
        symbol = arg.getString("Symbol");

        symbolTextView.setText(symbol);

        quote = new ArrayList<>();

        makeBold(YEAR);
        fillJsonData("1y");

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(DAY);
                getDayContent();
            }
        });
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(WEEK);
                getWeekContent();
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(MONTH);
                getMonthContent();
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(YEAR);
                getYearContent();
            }
        });
        fiveYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(FIVE_YEAR);
                getfiveYearContent();
            }
        });
        max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBold(MAX);
                getMaxContent();
            }
        });

    }

    public void makeBold(int no){
        day.setTypeface(Typeface.DEFAULT);
        week.setTypeface(Typeface.DEFAULT);
        year.setTypeface(Typeface.DEFAULT);
        fiveYear.setTypeface(Typeface.DEFAULT);
        max.setTypeface(Typeface.DEFAULT);
        month.setTypeface(Typeface.DEFAULT);

        switch (no){
            case DAY:
                day.setTypeface(null,Typeface.BOLD);
                break;
            case WEEK:
                week.setTypeface(null,Typeface.BOLD);
                break;
            case MONTH:
                month.setTypeface(null,Typeface.BOLD);
                break;
            case YEAR:
                year.setTypeface(null,Typeface.BOLD);
                break;
            case FIVE_YEAR:
                fiveYear.setTypeface(null,Typeface.BOLD);
                break;
            case MAX:
                max.setTypeface(null,Typeface.BOLD);
                break;
        }

    }

    public void getDayContent(){ fillJsonData("1d"); }
    public void getWeekContent(){ fillJsonData("7d"); }
    public void getMonthContent(){ fillJsonData("1m"); }
    public void getYearContent(){ fillJsonData("1y"); }
    public void getfiveYearContent(){ fillJsonData("5y"); }
    public void getMaxContent(){ fillJsonData("my"); }


    void fillJsonData(String range) {

        String url = "http://chartapi.finance.yahoo.com/instrument/1.0/" + symbol + "/chartdata;type=close;range=" +
                range + "/json";
        StringRequest req = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            response = response.replace("finance_charts_json_callback( ", "");
                            response = response.replace("\n", "");
                            JSONObject answer = new JSONObject(response.substring(0, response.length() - 2));
                            JSONArray quoteArray = answer.getJSONArray("series");
                            quote = new ArrayList<>();
                            for (int i = 0; i < quoteArray.length(); i++)
                                quote.add(quoteArray.getJSONObject(i));

                            createChart();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        Volley.newRequestQueue(this).add(req);
    }

    private void createChart() {

        chart.clearAnimation();

        chart.setBackgroundColor(Color.WHITE);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        boolean dateParam = quote.get(0).has("Date");
        for (int i = 0; i < quote.size(); i++) {
            try {
                entries.add(new Entry((float) quote.get(i).getDouble("close"), i));
                if(dateParam)
                    labels.add(quote.get(i).getString("Date"));
                else
                    labels.add(quote.get(i).getString("Timestamp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LineDataSet dataset = new LineDataSet(entries, "Stock Rate");
        LineData data = new LineData(labels, dataset);
        chart.setData(data);
        chart.animateX(1000);
    }


    private void garbageWork() {
        /*
        StringBuilder urlStringBuilder = new StringBuilder();
        try {

            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol = "
                    + symbol, "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("and startDate = \"" + startDate+"\"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("and endDate = \"" + endDate + "\"", "UTF-8"));

            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://query.yahooapis.com/v1/public/yql?" +
                "q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" +
                symbol +
                "%22%20and%20" +
                "startDate%20%3D%20%22" +
                startDate +
                "%22%20and%20" +
                "endDate%20%3D%20%22" +
                endDate +
                "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        Uri.Builder uri = new Uri.Builder();
        //try {
            uri.scheme("https")
                    .authority("query.yahooapis.com")
                    .appendPath("v1")
                    .appendPath("public")
                    .appendPath("yql")
                    .appendQueryParameter("q","select * from yahoo.finance.historicaldata where symbol = "+ symbol )
                    .appendQueryParameter("startDate",startDate)
                    .appendQueryParameter("endDate",endDate)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("env","store://datatables.org/alltableswithkeys")
                    .appendQueryParameter("callback","");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*

       / url = uri.build().toString();
        url = urlStringBuilder.toString();


        try {
            url = URLEncoder.encode("https://query.yahooapis.com/v1/public/yql?" +
                    "q=select * from yahoo.finance.historicaldata where " +
                    "symbol = \" "+ symbol +"\"" +
                    " and startDate = \" " + startDate + "\" " +
                    "and endDate = \" " + endDate + "\"" +
                    "&format=json&env=store://datatables.org/alltableswithkeys&callback=" , "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*


        String url1 = "https://query.yahooapis.com/v1/public/yql?" +
                "q=select * from yahoo.finance.historicaldata where symbol = \""+ symbol +"\" " +
                "and startDate = \"" + startDate +"\" " +
                "and endDate = \""+ endDate +"\"" +
                "&format=json&env=store://datatables.org/alltableswithkeys&callback=";

    */
    }
}
