package com.example.wadood.prob_project;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Sheet sheetData;
    final static int OPEN_EXCEL_FILE=0;
    private TextView minTV;
    private TextView maxTV;
    private TextView meanTV;
    private TextView modeTV;
    private TextView varianceTV;
    private TextView deviationTV;
    private Spinner columnSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFilePermissions();
        initGuiComponents();
    }

    private void initGuiComponents() {
        findViewById(R.id.file_button).setOnClickListener(this);
        findViewById(R.id.piechart).setOnClickListener(this);
        findViewById(R.id.barchart).setOnClickListener(this);
        findViewById(R.id.histogram).setOnClickListener(this);
        minTV = findViewById(R.id.min_textview);
        maxTV = findViewById(R.id.max_textview);
        meanTV = findViewById(R.id.mean_textview);
        modeTV = findViewById(R.id.mode_textview);
        varianceTV = findViewById(R.id.variance_textview);
        deviationTV = findViewById(R.id.deviation_textview);
        columnSpinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(
                this, R.array.no_column,
                android.R.layout.simple_spinner_item
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        columnSpinner.setAdapter(spinnerArrayAdapter);
    }

    private void startGraphActivity(String graph){
        Intent intent = new Intent(getBaseContext(), GraphActivity.class);
        intent.putExtra("GRAPH_TYPE", graph);
        intent.putExtra("SHEET_DATA", (new Gson()).toJson(sheetData));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.file_button: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.ms-excel");
                startActivityForResult(intent, OPEN_EXCEL_FILE);
                break;
            }
            case R.id.piechart: {
                startGraphActivity("PIE_CHART");
                break;
            }
            case R.id.barchart: {
                startGraphActivity("BARCHART");
                break;
            }
            case R.id.histogram: {
                startGraphActivity("HISTOGRAM");
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case OPEN_EXCEL_FILE:
            {
                readExcelData(data.getData().getPath());
                initSpinner();
                break;
            }
            default:
                break;
        }
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

    private void displayDiscription(int activeColumn) {
        DecimalFormat formatter = new DecimalFormat("#0.000");
        Column column = sheetData.getColumn(activeColumn);
        minTV.setText(Double.toString(column.min()));
        maxTV.setText(Double.toString(column.max()));
        meanTV.setText(formatter.format(column.mean()));
        modeTV.setText(Double.toString(column.mode()));
        varianceTV.setText(formatter.format(column.variance()));
        deviationTV.setText(formatter.format(column.deviation()));
    }

    private void readExcelData(String filePath) {
        File inputFile = new File(filePath);
        try {
            InputStream inputStream = new FileInputStream(inputFile);
            Workbook workbook = WorkbookFactory.create(inputStream);
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Row headers = sheet.getRow(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            int colsCount = headers.getPhysicalNumberOfCells();

            sheetData = new Sheet(colsCount);

            for(int c = 0; c < colsCount; c++) {
                String value = getCellAsString(headers, c, formulaEvaluator);
                sheetData.setColumn(c, new Column(value));
            }
            for(int r = 1; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                for(int c = 0; c < colsCount; c++) {
                    String value = getCellAsString(row, c, formulaEvaluator);
                    sheetData.getColumn(c).insertRow(r, Double.parseDouble(value));
                }
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "readExcelData: FileNotFoundException. " + e.getMessage() );
        } catch (IOException e) {
            Log.e(TAG, "readExcelData: Error reading inputstream. " + e.getMessage() );
        } catch (InvalidFormatException e) {
            Log.e(TAG, "readExcelData: InvalidFormatException. " + e.getMessage());
        }
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    value = ""+cellValue.getNumberValue();
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "getCellAsString: NullPointerException: " + e.getMessage() );
        }
        return value;
    }

    private void checkFilePermissions() {
        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCheck != 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        displayDiscription(position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}


