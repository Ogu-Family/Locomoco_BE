package org.prgms.locomocoserver.global.common;

import lombok.Getter;

@Getter
public class Pair<T, R> {
    T t;
    R r;

    public Pair(T t, R r) {
        this.t = t;
        this.r = r;
    }
}
