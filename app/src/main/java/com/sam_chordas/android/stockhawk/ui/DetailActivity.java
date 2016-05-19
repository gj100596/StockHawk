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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    TextView day, week, month, year, fiveYear, max, symbolTextView;

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
        symbol = arg.getString(getString(R.string.symbol_intent_keyword));

        symbolTextView.setText(symbol);

        quote = new ArrayList<>();

        makeBold(MONTH);
        fillJsonData("1m");

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

    public void makeBold(int no) {
        day.setTypeface(Typeface.DEFAULT);
        week.setTypeface(Typeface.DEFAULT);
        year.setTypeface(Typeface.DEFAULT);
        fiveYear.setTypeface(Typeface.DEFAULT);
        max.setTypeface(Typeface.DEFAULT);
        month.setTypeface(Typeface.DEFAULT);

        day.setTextColor(Color.WHITE);
        week.setTextColor(Color.WHITE);
        year.setTextColor(Color.WHITE);
        fiveYear.setTextColor(Color.WHITE);
        max.setTextColor(Color.WHITE);
        month.setTextColor(Color.WHITE);

        switch (no) {
            case DAY:
                day.setTypeface(null, Typeface.BOLD);
                day.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
            case WEEK:
                week.setTypeface(null, Typeface.BOLD);
                week.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
            case MONTH:
                month.setTypeface(null, Typeface.BOLD);
                month.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
            case YEAR:
                year.setTypeface(null, Typeface.BOLD);
                year.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
            case FIVE_YEAR:
                fiveYear.setTypeface(null, Typeface.BOLD);
                fiveYear.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
            case MAX:
                max.setTypeface(null, Typeface.BOLD);
                max.setTextColor(getResources().getColor(R.color.material_blue_500));
                break;
        }

    }

    public void getDayContent() {
        fillJsonData("1d");
    }

    public void getWeekContent() {
        fillJsonData("7d");
    }

    public void getMonthContent() {
        fillJsonData("1m");
    }

    public void getYearContent() {
        fillJsonData("1y");
    }

    public void getfiveYearContent() {
        fillJsonData("5y");
    }

    public void getMaxContent() {
        fillJsonData("my");
    }


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
        chart.setDescription("");
        chart.setNoDataText(getString(R.string.loading_chart));
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        boolean dateParam = quote.get(0).has(getString(R.string.date_result_keyword));
        for (int i = 0; i < quote.size(); i++) {
            try {
                entries.add(new Entry((float) quote.get(i).getDouble(getString(R.string.close_result_keyword)), i));
                StringBuilder yAxisText;
                if (dateParam) {
                    yAxisText = new StringBuilder(
                            quote.get(i).getString(getString(R.string.date_result_keyword)));
                    yAxisText.insert(4, "-");
                    yAxisText.insert(7, "-");
                } else {
                    Timestamp ts = new Timestamp(Long.parseLong(quote.get(i).getString(getString(R.string.timestamp_result_keyword))));
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(ts.getTime()*1000));
                    yAxisText = new StringBuilder(c.getTime().toString().substring(4,16));

                }

                labels.add(yAxisText.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LineDataSet dataset = new LineDataSet(entries, getString(R.string.stock_result_keyword));
        LineData data = new LineData(labels, dataset);
        chart.setData(data);
        chart.animateX(1000);
    }

}
