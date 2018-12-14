package com.example.wadood.prob_project;

import java.util.HashMap;

public class Sheet {
    private Column[] columns;

    public Sheet(int col){
        columns = new Column[col];
    }

    public void setColumn(int col, Column column){
        columns[col] = column;
    }

    public Column getColumn(String header) {
        for(int i=0; i < columns.length; i++){
            if(columns[i].getHeader() == header)
            {
                return columns[i];
            }
        }
        return null;
    }

    public Column getColumn(int col_no) {
        return columns[col_no];
    }

    public String[] getColumnNames() {
        String[] names = new String[columns.length];
        for(int i=0; i<columns.length; i++){
            names[i] = columns[i].getHeader();
        }
        return names;
    }

    public String[] getOptions(String option) {
        String[] options = new String[columns.length+1];
        options[0] = option;
        for(int i=0; i<columns.length; i++){
            options[i+1] = columns[i].getHeader();
        }
        return options;
    }
    public HashMap<String, Double> getHashMap(int col1, int col2){
        HashMap<String, Double> hashMap = new HashMap<>();
        if (col1==col2){
            for(int i=0; i < columns[col1].length(); i++) {
                String key = columns[col1].getKey(i);
                if (hashMap.containsKey(key)) {
                    double newValue = hashMap.get(key) + 1;
                    hashMap.put(key, newValue);
                } else {
                    hashMap.put(key, 1.0);
                }
            }
        }
        else{
            for(int i=0; i < columns[col1].length(); i++) {
                double value = columns[col1].getRow(i);
                String key = columns[col2].getKey(i);
                if (hashMap.containsKey(key)) {
                    double newValue = hashMap.get(key) + value;
                    hashMap.put(key, newValue);
                } else {
                    hashMap.put(key, value);
                }
            }
        }
        return hashMap;
    }
}
