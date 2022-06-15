package com.example.amemo.ui.memo;

public class MemoItem {
    private String name;
    public int remindLevel;
    public MemoItem(String name) {
        this.name = name;
        remindLevel = 0;
    }

    public String getName() {
        return name;
    }

}