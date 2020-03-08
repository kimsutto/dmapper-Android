package com.fixer.dmapper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InsertUser extends Thread {

    public boolean active = true;
    Handler mHandler;
    String url = null;

    public InsertUser(String user) {
        mHandler = new Handler(Looper.getMainLooper());
        String userdb = "?" + user;

        url = "http://52.79.214.170/add_user.php" + userdb;
        Log.e("add to user", url);
    }

    @Override
    public void run() {
        super.run();
        if (active) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) phpUrl.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    //conn.setRequestProperty("Content-Length", Integer.toString(url.length()));

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                Log.e("insertUser", "success" + jsonHtml.toString() + "end");
            } catch (Exception e) {
                Log.e("insertUser", "fail" + e.toString());
            }

        }
    }
}
