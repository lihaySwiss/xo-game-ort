package com.example.xoo;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {
    private Socket socket;

    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectInputStream getIs() {
        return is;
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    @Override
    public String toString() {
        return socket.toString();
    }

    @Override
    public void close() throws IOException {
        is.close();
        os.close();
        socket.close();
    }
}
