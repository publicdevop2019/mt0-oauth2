package com.hw.shared.sql;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SumPagedRep<T> {
    protected List<T> data = new ArrayList<>();
    protected Long totalItemCount;

    public SumPagedRep(List<T> data, Long aLong) {
        this.data = data;
        this.totalItemCount = aLong;
    }

    public SumPagedRep() {
    }
}
