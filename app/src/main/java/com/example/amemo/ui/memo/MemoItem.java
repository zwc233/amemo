package com.example.amemo.ui.memo;

import com.example.amemo.CacheHandler;

public class MemoItem {
    public CacheHandler.Memo memo;
    public int remindLevel;

    public MemoItem(CacheHandler.Memo memo) {
        this.memo = memo;
        CacheHandler.User user = CacheHandler.getUser();
        if (user.emphasizedMemos.contains(memo.id)) {
            remindLevel = 2;
        } else if (user.notedMemos.contains(memo.id)) {
            remindLevel = 1;
        } else {
            remindLevel = 0;
        }
    }
}