package com.example.xoo;

import java.io.Serializable;
import java.util.Objects;

class Request implements Serializable {
    private final int size;
    private final String name;

    Request(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Request) obj;
        return this.size == that.size &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, name);
    }

    @Override
    public String toString() {
        return "Request[" +
                "difficulty=" + size + ", " +
                "name=" + name + ']';
    }
}
