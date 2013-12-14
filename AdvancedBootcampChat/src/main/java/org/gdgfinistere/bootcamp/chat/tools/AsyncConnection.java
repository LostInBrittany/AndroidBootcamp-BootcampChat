package org.gdgfinistere.bootcamp.chat.tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by horacio on 12/13/13.
 */
public abstract class AsyncConnection extends AsyncTask<String, Void, String> {

    /**
     * Utility method to test if connection is available
     * @param activity
     * @return connection available
     */
    public static boolean haveConnection(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    protected String connect(String urlString, String method,  HashMap<String, String> parameters)  {

        // Un stream pour récevoir la réponse
        InputStream is = null;

        try {
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            // Parameters are passed on URL for this example...
            StringBuilder sb = new StringBuilder(urlString);
            if ((null != parameters) && (parameters.size() > 0)) {
                for (Map.Entry<String,String> entry: parameters.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                sb.deleteCharAt(sb.length()-1);
            }

            URL url = new URL(sb.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int status = conn.getResponseCode();
            Log.d("AsyncConnection", "The response status code is: " + status);

            if (status != 200) {
                is = conn.getErrorStream();
                Log.d("AsyncConnection", "Got ErrorStream");
            } else {
                is = conn.getInputStream();
                if ((conn.getContentLength() > len)) {
                    len =  conn.getContentLength();
                }
                Log.d("AsyncConnection", "Got InputStream, Content-Length: "+len);
            }





            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            
            Log.d("AsyncConnection", "Status: "+status+" Content: "+contentAsString);
            if (status != 200) {
                return "{\"status\":\""+status+"\",\"error\":\""+contentAsString+"\"}";
            }
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {
            Log.e("AsyncConnection", e.getMessage());
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e("AsyncConnection", e.getMessage());
                }
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[100];

        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        int charRead;
        do {
            charRead = reader.read(buffer);
            if (charRead >0) {
                sb.append(buffer, 0, charRead);
            }
        } while (charRead >0 );

        return sb.toString().trim();
    }


}
