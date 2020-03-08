package com.fixer.dmapper.KakaoGeoCodingManager;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Searcher {

    public static final String LOCAL_KEYWORD_SEARCH_API_FORMAT = "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&radius=%d&page=%d&apikey=%s";
    public OnFinishSearchListener onFinishSearchListener;
    SearchTask searchTask;

    public void searchKeyword(String query,int radius, int page, String apikey, OnFinishSearchListener onFinishSearchListener) {
        this.onFinishSearchListener = onFinishSearchListener;
        System.out.println("넘어오긴하냐?");
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
        String url = buildKeywordSearchApiUrlString(query,radius, page, apikey);
        searchTask = new SearchTask();
        searchTask.execute(url);
    }

    private String buildKeywordSearchApiUrlString(String query,int radius, int page, String apikey) {
        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(Locale.ENGLISH, LOCAL_KEYWORD_SEARCH_API_FORMAT, encodedQuery, radius, page, apikey);
    }

    private String fetchData(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000 /* milliseconds */);
            conn.setConnectTimeout(7000 /* milliseconds */);
            conn.setRequestMethod("GET"); // GET 방식으로  API 요청
            conn.setRequestProperty("Authorization", "KakaoAK "+MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); // header 부분에 앱키 작성
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            @SuppressWarnings("resource")
            Scanner s = new Scanner(is);
            s.useDelimiter("\\A");
            String data = s.hasNext() ? s.next() : "";

            Log.w("data : ", data);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private List<KakaoContainer> parse(String jsonString) {
        List<KakaoContainer> itemList = new ArrayList<KakaoContainer>();
        try {
            JSONObject reader = new JSONObject(jsonString);
            JSONArray objects = reader.getJSONArray("documents");
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                //notice_item 클래스에 json 데이터 할당.
                KakaoContainer item = new KakaoContainer();
                item.place_name = object.getString("place_name");
                item.address_name = object.getString("address_name");
                item.x = object.getDouble("x");
                item.y = object.getDouble("y");
                System.out.println(item.place_name);
                itemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(itemList);
        return itemList;
    }

    public class SearchTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = strings[0];
            return fetchData(data);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            onFinishSearchListener.onSuccess(parse(s));
        }
    }
}
