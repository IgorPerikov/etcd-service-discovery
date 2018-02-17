package com.github.igorperikov.etcd.discovery.domain;

class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    T1 getFirst() {
        return first;
    }

    T2 getSecond() {
        return second;
    }
}
