package com.example.wadood.prob_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;

public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Sheet sheetData;
    private Spinner columnSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Context Context = GraphActivity.this;
        String sheetJSON = getIntent().getStringExtra("SHEET_DATA");
        sheetData = (new Gson()).fromJson(sheetJSON, Sheet.class);
        String graphType = getIntent().getStringExtra("GRAPH_TYPE");
        Toast.makeText(Context, graphType, Toast.LENGTH_LONG).show();
    }
    private void initGuiComponents(){
        columnSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(
                this, R.array.no_column,
                android.R.layout.simple_spinner_item
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                sheetData.getColumnNames()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(adapter);
        columnSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(GraphActivity.this, position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
