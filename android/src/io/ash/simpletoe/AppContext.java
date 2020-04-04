package io.ash.simpletoe;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class AppContext extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.SERVER_URI);
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return inited socket
     */
    public Socket getSocket() {
        return mSocket;
    }
}
