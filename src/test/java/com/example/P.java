package com.example;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * @author bootystar
 */
@Data
public class P<T> {
    private List<T> records;

    public P(IPage<T> data) {
        records = data.getRecords();
    }

    public static <T> P<T> of(IPage page) {
        return new P(page);
    }
}
