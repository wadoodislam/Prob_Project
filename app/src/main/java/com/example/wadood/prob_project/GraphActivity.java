package com.example.wadood.prob_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

public class GraphActivity extends AppCompatActivity {
    private Sheet sheetData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Context Context = GraphActivity.this;
        String sheetJSON = getIntent().getStringExtra("SHEET_DATA");
        sheetData = (new Gson()).fromJson(sheetJSON, Sheet.class);
        String graphType = getIntent().getStringExtra("GRAPH_TYPE");
        Toast.makeText(GraphActivity.this, graphType, Toast.LENGTH_LONG).show();
        LineChart chart = new LineChart(Context);
        LinearLayout linearLayout = findViewById(R.id.graph_layout);
        linearLayout.addView(chart);
    }
}
