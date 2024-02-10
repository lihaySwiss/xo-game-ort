package com.example.xoo;

import java.io.Serializable;

public class Response implements Serializable {
        public enum Type{
                ACCEPTED_MOVE(null),
                GAME_FOUND(null),
                ILLEGAL_MOVE("Illegal move"),
                INVALID_DIFF("Difficulty not in range");

                Type(String msg){
                    this.msg = msg;
                }
                private String msg;
        }

        Response(Type type){
                this.type = type;
        }

        public void setData(Object data) {
                this.data = data;
        }

        public Object getData() {
                return data;
        }

        public Type getType() {
                return type;
        }

        private Type type;
        private Object data;
}
