package com.example.xoo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ServerRequest extends Request {
    private Connection conn;


    ServerRequest(int size, String name, Connection conn) throws IOException {
        super(size, name);
        this.conn = conn;
    }

    public Connection getConnection() {
        return conn;
    }
}
