package com.github.igorperikov.etcd.discovery.utility;

public class Pair<T, T1> {
    private final T first;
    private final T1 second;

    public Pair(T first, T1 second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T1 getSecond() {
        return second;
    }
}
