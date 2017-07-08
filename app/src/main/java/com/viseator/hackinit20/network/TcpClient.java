package com.viseator.hackinit20.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * Created by viseator on 2016/12/23.
 * Wudi
 * viseator@gmail.com
 */

public class TcpClient {
    private static final String TAG = "@vir TcpClient";
    public static int SERVER_PORT = 7889;
    private Object obj;
    private Thread thread;
    private String ipAddress;
    private Handler handler;


    class SendData implements Runnable {
        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(ipAddress, SERVER_PORT);
                socket.setReuseAddress(true);
                socket.setKeepAlive(true);
                socket.setSoTimeout(5000);

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(obj);

                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object resultObject =  objectInputStream.readObject();

                Message msg = new Message();
                msg.what = SERVER_PORT;
                msg.obj = resultObject;
                handler.sendMessage(msg);
            } catch (SocketTimeoutException e) {
                Log.d(TAG, "resend");
                try {
                    if (socket != null) socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                sendRequest(ipAddress, obj, handler);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRequest(String ipAddress, Object obj, Handler handler) {
        this.ipAddress = ipAddress;
        this.obj = obj;
        this.handler = handler;
        thread = new Thread(new SendData());
        thread.start();
    }
}
