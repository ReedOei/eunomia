package com.reedoei.eunomia.latex;

public interface Cell<T> {
    T val();

    Cell<T> showOverride(final String showOverride);

    String toString();
}
