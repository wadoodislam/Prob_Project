package com.example.wadood.prob_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Sheet sheetData;
    private Spinner columnSpinner1;
    private Spinner columnSpinner2;
    private int activeCol1 = -1;
    private int activeCol2 = -1;
    private Chart chart;
    private String graphType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Context Context = GraphActivity.this;

        String sheetJSON = getIntent().getStringExtra("SHEET_DATA");
        sheetData = (new Gson()).fromJson(sheetJSON, Sheet.class);
        graphType = getIntent().getStringExtra("GRAPH_TYPE");

        initGuiComponents();

        Toast.makeText(Context, graphType, Toast.LENGTH_LONG).show();
    }
    @SuppressLint("SetTextI18n")
    private void initGuiComponents(){
        ((TextView) findViewById(R.id.label_col2)).setText("with relation to:");

        initSpinners();

        switch (graphType) {
            case "PIE_CHART": {
                ((TextView)findViewById(R.id.label_col1)).setText("Pie Chart for:");
                chart = new PieChart(GraphActivity.this);
                break;
            }
            case "BAR_CHART": {
                ((TextView)findViewById(R.id.label_col1)).setText("Bar Chart for:");
                chart = new BarChart(GraphActivity.this);
                break;
            }
            case "HISTOGRAM": {
                ((TextView)findViewById(R.id.label_col1)).setText("Histogram for:");
                chart = new LineChart(GraphActivity.this);
                findViewById(R.id.label_col2).setVisibility(View.GONE);
                columnSpinner2.setVisibility(View.GONE);
                break;
            }
        }
        ((FrameLayout)findViewById(R.id.graph)).addView(chart);
    }

    private void initSpinners() {
        columnSpinner1 = findViewById(R.id.spinner_col1);
        columnSpinner2 = findViewById(R.id.spinner_col2);
        ArrayAdapter<CharSequence> adapter;

        if(sheetData==null){
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.no_file,
                    android.R.layout.simple_spinner_item
            );
        }
        else{
            adapter = new ArrayAdapter<CharSequence>(
                    this, android.R.layout.simple_spinner_item,
                    sheetData.getOptions("No Column Selected")
            );
            columnSpinner1.setOnItemSelectedListener(this);
            columnSpinner2.setOnItemSelectedListener(this);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner1.setAdapter(adapter);
        columnSpinner2.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        switch(adapterView.getId()){
            case R.id.spinner_col1: {
                activeCol1 = --position;
                break;
            }
            case R.id.spinner_col2: {
                activeCol2 = --position;
                break;
            }
        }
        renderGraph();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void renderGraph() {
        if (activeCol1 == -1 || activeCol2 == -1){
           return;
        }
        String dataLabel = sheetData.getColumn(activeCol1).getHeader();
        String relationLable = sheetData.getColumn(activeCol2).getHeader();
        Description desc = new Description();
        desc.setText(dataLabel +" in relation to " + relationLable);
        chart.setDescription(desc);
        switch (graphType) {
            case "PIE_CHART": {
                ArrayList<PieEntry> entries = new ArrayList<>();

                for (Map.Entry entry:
                        sheetData.getHashMap(activeCol1, activeCol2).entrySet()) {
                    float value = Float.parseFloat(entry.getValue().toString());
                    String label = relationLable + " " + entry.getKey();
                    entries.add(new PieEntry(value, label));
                }

                PieDataSet pieDataSet = new PieDataSet(entries, dataLabel);
                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                PieData pieData = new PieData(pieDataSet);
                pieData.setValueTextSize(10f);
                pieData.setValueTextColor(Color.RED);

                chart.setData(pieData);
                chart.invalidate();
                break;
            }
            case "BAR_CHART": {
                List<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0f, 30f));
                entries.add(new BarEntry(1f, 80f));
                entries.add(new BarEntry(2f, 60f));
                entries.add(new BarEntry(3f, 50f));
                // gap of 2f
                entries.add(new BarEntry(5f, 70f));
                entries.add(new BarEntry(6f, 60f));

                BarDataSet set = new BarDataSet(entries, "BarDataSet");

                BarData data = new BarData(set);
                data.setBarWidth(0.9f); // set custom bar width
                chart.setData(data);
                chart.setFitsSystemWindows(true); // make the x-axis fit exactly all bars
                chart.invalidate(); // refresh
                break;
            }
            case "HISTOGRAM": {

                break;
            }
        }
    }
}
