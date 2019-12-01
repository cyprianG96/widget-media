package com.mobica.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestThread extends Thread {
    private String reqUrl;
    private OnHttpRequestListener listener;

    public HttpRequestThread(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public void start(OnHttpRequestListener listener) {
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        // Maintain http url connection.
        HttpURLConnection httpConn = null;
        // Read text input stream.
        InputStreamReader isReader = null;
        // Read text into buffer.
        BufferedReader bufReader = null;
        // Save server response text.
        StringBuffer readTextBuf = new StringBuffer();

        try {
            // Create a URL object use page url.
            URL url = new URL(reqUrl);
            // Open http connection to web server.
            httpConn = (HttpURLConnection) url.openConnection();
            // Set http request method to get.
            httpConn.setRequestMethod("GET");
            // Set connection timeout and read timeout value.
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);

            // Get input stream from web url connection.
            InputStream inputStream = httpConn.getInputStream();
            // Create input stream reader based on url connection input stream.
            isReader = new InputStreamReader(inputStream);
            // Create buffered reader.
            bufReader = new BufferedReader(isReader);
            // Read line of text from server response.
            String line = bufReader.readLine();

            // Loop while return line is not null.
            while(line != null) {
                // Append the text to string buffer.
                readTextBuf.append(line);
                // Continue to read text line.
                line = bufReader.readLine();
            }

            listener.onHttpResponseOk(readTextBuf.toString());

        } catch(MalformedURLException ex) {
            ex.printStackTrace();
            listener.onHttpResponseError(0, ex.getMessage());
        } catch(IOException ex) {
            ex.printStackTrace();
            listener.onHttpResponseError(0, ex.getMessage());
        } catch(Throwable ex) {
            ex.printStackTrace();
            listener.onHttpResponseError(0, ex.getMessage());
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                    bufReader = null;
                }

                if (isReader != null) {
                    isReader.close();
                    isReader = null;
                }

                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                listener.onHttpResponseError(0, ex.getMessage());
            }
        }
    }

    public interface OnHttpRequestListener {
        void onHttpResponseOk(String response);
        void onHttpResponseError(int errorCode, String response);
    }
}
