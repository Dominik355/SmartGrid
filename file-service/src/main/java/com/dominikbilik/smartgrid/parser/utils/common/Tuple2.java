package com.dominikbilik.smartgrid.parser.utils.common;

import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class Tuple2<T1, T2> implements Iterable<Object>, Serializable {

    @NonNull final T1 t1;
    @NonNull final T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        this.t1 = Objects.requireNonNull(t1, "t1 can not be null");
        this.t2 = Objects.requireNonNull(t2, "t2 can not be null");
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public <R> Tuple2<R, T2> mapT1(Function<T1, R> mapper) {
        return new Tuple2<>(mapper.apply(t1), t2);
    }

    public <R> Tuple2<T1, R> mapT2(Function<T2, R> mapper) {
        return new Tuple2<>(t1, mapper.apply(t2));
    }

    public Object get(int index) {
        switch (index) {
            case 0:
                return t1;
            case 1:
                return t2;
            default:
                return null;
        }
    }

    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    public Object[] toArray() {
        return new Object[]{t1, t2};
    }

    @Override
    public int hashCode() {
        int result = size();
        result = 21 * result + t1.hashCode();
        result = 21 * result + t2.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        return t1.equals(tuple2.t1) && t2.equals(tuple2.t2);
    }

    @Override
    public String toString() {
        return "[T1=" + t1 + ",|T2=" + t2 + "]";
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableList(toList()).iterator();
    }

    public int size() {
        return 2;
    }
}
