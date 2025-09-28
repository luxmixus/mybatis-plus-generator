package com.example;

import lombok.Data;

/**
 * @author bootystar
 */
@Data
public class R<T> {
    private T data;
    public R(T data) {
        this.data = data;
    }
    public static <T> R<T> of(T data) {
        return new R<>(data);
    }
}
