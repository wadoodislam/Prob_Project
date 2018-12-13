package com.example.wadood.prob_project;
import java.util.Collections;
import java.util.ArrayList;

public class Column {
    private String header;
    private ArrayList<Double> cells;

    Column(String header){
        this.header = header;
        cells = new ArrayList<>();

    }
    public void insertRow(double entry)
    {
        cells.add(entry);
    }
    public double getRow(int row)
    {
        return cells.get(row);
    }
    public int length(){
        return cells.size();
    }
    public double min(){
        return Collections.min(cells);
    }
    public double max(){
        return Collections.max(cells);
    }
    public double mean(){
        double sum = 0;
        for (double cell : cells) {
            sum += cell;
        }
        return sum/cells.size();
    }
    public double mode(){
        double mode = 0;
        int count = 0;

        for ( double i : cells ){
            int tempCount = 1;

            for(double e : cells){
                if( i == e)
                    tempCount++;

                if( tempCount > count){
                    count = tempCount;
                    mode = i;
                }
            }
        }
        return mode;
    }
    public double variance(){
        double xBar = this.mean();
        double sum = 0;
        for (double cell : cells) {
            sum += Math.pow(cell-xBar,2);
        }
        return sum/cells.size();
    }
    public double deviation(){
        return Math.sqrt(this.variance());
    }
    public String getHeader() {
        return header;
    }

    public String getKey(int i) {
        return Double.toString(cells.get(i));
    }
}
