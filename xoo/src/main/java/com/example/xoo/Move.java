package com.example.xoo;

import java.io.Serializable;

public record Move(int x, int y, boolean terminate, boolean win) implements Serializable {
    public Move(int x, int y){this(x, y, false, false);}
}