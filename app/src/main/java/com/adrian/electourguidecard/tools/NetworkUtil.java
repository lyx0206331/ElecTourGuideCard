package com.adrian.electourguidecard.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by adrian on 16-5-31.
 */
public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getName();

    private static final String DOMAIN = "61.50.105.142";
    private static final int PORT = 10002;

    private OnGetServerDataListener listener;

    public NetworkUtil(OnGetServerDataListener listener) {
        this.listener = listener;
    }

    public OnGetServerDataListener getListener() {
        return listener;
    }

    public void setListener(OnGetServerDataListener listener) {
        this.listener = listener;
    }

    public void getData(final String flag) {
        CommUtil.e(TAG, "cmd : " + flag);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                InputStream is = null;
                OutputStream os = null;
                try {
                    socket = new Socket(DOMAIN, PORT);
                    socket.setSoTimeout(30000);
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    os.write(flag.getBytes());
                    os.flush();
                    int len = 0;
                    byte[] tmp = new byte[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((len = is.read(tmp)) != -1) {
                        CommUtil.e(TAG, "len : " + len);
                        sb.append(new String(tmp, 0, len));
                    }
                    CommUtil.e(TAG, "data : " + sb);
                    if (listener != null) {
                        listener.onGetServerData(flag, sb.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onException(0);
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (os != null) {
                            os.close();
                        }
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public interface OnGetServerDataListener {
        void onGetServerData(String flag, String data);
        void onException(int errorCode);
    }
}
