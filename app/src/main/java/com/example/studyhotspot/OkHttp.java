package com.example.studyhotspot;


import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp {
    // one instance, reuse
    private OkHttpClient httpClient = new OkHttpClient();

    public String getURL(String id) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("https://data.gov.sg/api/action/package_show?id=");
        sb.append(id);
        final String[] url = {sb.toString()};

        Request request = new Request.Builder()
                .url(url[0])
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                // Get response body
                String jsonData = response.body().string();

                try {
                    JSONObject Jobject = new JSONObject(jsonData);
                    url[0] = Jobject.getJSONObject("result").getJSONArray("resources").getJSONObject(1).get("url").toString();
                }catch (Exception e){ }
            }
        });
        System.out.println("HELLO: "+ url[0]);
        return (url[0]);

/*        System.out.println("HELLO2");
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Get response body
        String jsonData = response.body().string();

        JSONObject Jobject = new JSONObject(jsonData);

        url = Jobject.getJSONObject("result").getJSONArray("resources").getJSONObject(1).get("url").toString();
            // if want data in kml format: set getJSONObject(0)
            // if want data in geojson format: set getJSONObject(1)

        return(url);*/
    }

    public JSONObject accessData(String dataURL) throws Exception {
        Request request = new Request.Builder()
                .url(dataURL)
                .build();

        final JSONObject[] result = new JSONObject[1];
        result[0]=null;

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                // Get response body
                String jsonData = response.body().string();

                try {
                    JSONObject Jobject = new JSONObject(jsonData);
                    result[0] = Jobject;
                }catch (Exception e){ }

                /*if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try {
                    jObject[0] = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
        while (result[0]==null);
        return(result[0]);
    }
}
