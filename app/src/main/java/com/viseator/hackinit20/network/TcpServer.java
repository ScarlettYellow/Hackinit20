package com.viseator.hackinit20.network;

import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;

import com.viseator.hackinit20.util.ConvertData;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by viseator on 2016/12/23.
 * Wudi
 * viseator@gmail.com
 */

public class TcpServer {
    public static final int SERVER_PORT = 7889;
    public static final int RECEIVE_REQUEST = 110;
    private Thread thread;
    private Handler handler;



    class RunServer implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(SERVER_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream;
                    inputStream = socket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                    Object obj =  objectInputStream.readObject();
                    Message msg = Message.obtain();
                    msg.what = RECEIVE_REQUEST;
                    msg.obj = obj;
                    handler.sendMessage(msg);
                    objectInputStream.close();
                    inputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void startServer(Handler handler) {
        this.handler = handler;
        thread = new Thread(new RunServer());
        thread.start();
    }
}
