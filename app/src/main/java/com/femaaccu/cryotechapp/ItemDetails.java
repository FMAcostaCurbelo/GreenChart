package com.femaaccu.cryotechapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.femaaccu.cryotechapp.databinding.ActivityItemDetailsBinding;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

public class ItemDetails extends AppCompatActivity {
    private ActivityItemDetailsBinding binding;
    Bundle extras;
    Button bDay, bWeek, bMonth, bYear, bMax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        binding = ActivityItemDetailsBinding.inflate(getLayoutInflater());


        extras = getIntent().getExtras();
        View view = binding.getRoot();
        setContentView(view);


        String currencyId = extras.getString("currencyId");



        bDay = binding.buttonChartDay;
        bWeek = binding.buttonChartWeek;
        bMonth = binding.buttonChartMonth;
        bYear = binding.buttonChartYear;
        bMax = binding.buttonChartMax;

        setCandleStickChart(currencyId, "1");

        bDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setCandleStickChart(currencyId, "1");

            }
        });
        bWeek.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setCandleStickChart(currencyId, "7");

            }
        });
        bMonth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                setCandleStickChart(currencyId, "30");
            }
        });
        bYear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                setCandleStickChart(currencyId, "365");
            }
        });
        bMax.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                setCandleStickChart(currencyId, "max");
            }
        });




    }


    public void setCandleStickChart(String currencyID, String days){


        String url = "https://api.coingecko.com/api/v3/coins/"+currencyID+"/ohlc?vs_currency=eur&days="+days;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>()
                {
                   @Override
                    public void onResponse(JSONArray response) {
                        // display response

                        ArrayList<CandleEntry> candlestickentry = new ArrayList<>();
                        ArrayList<String> xvalue = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONArray array = response.getJSONArray(i);
                                String epochTime = array.getString(0);
                                Date time = new Date(Long.parseLong(epochTime));
                                String open = array.getString(1);
                                String high = array.getString(2);
                                String low = array.getString(3);
                                String close = array.getString(4);

                                float floatOpen = Float.parseFloat(open);
                                float floatHigh = Float.parseFloat(high);
                                float floatLow = Float.parseFloat(low);
                                float floatClose = Float.parseFloat(close);

                                switch (days){
                                    case "1":
                                        xvalue.add(""+time.getHours()+":"+time.getMinutes());
                                        break;
                                    case "7":
                                        xvalue.add(""+time.getDay()+":"+time.getHours());
                                        break;
                                    case "30":
                                        xvalue.add(""+time.getDay());
                                        break;
                                    case "365":
                                        xvalue.add(""+time.getMonth());
                                        break;
                                    case "max":
                                        xvalue.add(""+time.getYear());
                                        break;
                                    default:
                                        xvalue.add("error");
                                        break;
                                }



                                candlestickentry.add(new CandleEntry(i, floatHigh, floatLow, floatOpen, floatClose));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        String currencyname = extras.getString("currencyName");
                        CandleDataSet candledataset = new CandleDataSet(candlestickentry, currencyname);


                        candledataset.setColor(Color.rgb(80,80,80));
                        candledataset.setShadowColor(android.R.color.holo_blue_bright);
                        candledataset.setShadowWidth(1f);
                        candledataset.setDecreasingColor(Color.RED);
                        candledataset.setDecreasingPaintStyle(Paint.Style.FILL);
                        candledataset.setIncreasingColor(Color.GREEN);
                        candledataset.setIncreasingPaintStyle(Paint.Style.FILL);


                        CandleData candledata = new CandleData(xvalue, candledataset);

                        CandleStickChart candlechart = binding.candlechart;
                        candlechart.setData(candledata);
                        candlechart.setBackgroundColor(Color.WHITE);
                        candlechart.animateXY(2000,2000);

                        XAxis xval = candlechart.getXAxis();
                        xval.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xval.setDrawGridLines(false);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ItemDetails.this, "ERROR IS"+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
             // add it to the RequestQueue
            queue.add(getRequest);
    }



}