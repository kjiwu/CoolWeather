package com.kjiwu.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kjiwu on 16/6/13.
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while(null != (line = reader.readLine())) {
                        response.append(line);
                    }

                    if(null != listener) {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e) {
                    if(null != listener) {
                        listener.onError(e);
                    }
                }
                finally {
                    if(null != connection) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}
