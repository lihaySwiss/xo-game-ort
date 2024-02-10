package com.example.xoo;

import java.io.Serializable;

public record GameDetails(int size,
                          String name,
                          String opponentName,
                          byte mark,
                          byte opponentMark,
                          boolean firstMove) implements Serializable {
}
