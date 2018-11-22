package com.example.wadood.prob_project;

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
}
