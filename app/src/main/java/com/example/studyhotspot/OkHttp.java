package com.example.studyhotspot;


import java.io.IOException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp {
    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();

    public static void main(String[] args) throws Exception {

        OkHttp obj = new OkHttp();

        System.out.println("CKAN Package Show");

        // id of the wireless hotspots on data.gov.sg is 6b3f1e1b-257d-4d49-8142-1b2271d20143
        String dataURL = obj.getURL("6b3f1e1b-257d-4d49-8142-1b2271d20143");
        String data = obj.accessData(dataURL);
    }

    private String getURL(String id) throws Exception {

        Request request = new Request.Builder()
                .url("https://data.gov.sg/api/action/package_show?id=" + id)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Get response body
            String jsonData = response.body().string();

            JSONObject Jobject = new JSONObject(jsonData);

            String url = Jobject.getJSONObject("result").getJSONArray("resources").getJSONObject(1).get("url").toString();
            // if want data in kml format: set getJSONObject(0)
            // if want data in geojson format: set getJSONObject(1)

            return(url);
        }
    }

    private String accessData(String dataURL) throws Exception {
        Request request = new Request.Builder()
                .url(dataURL)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return(response.body().string());
        }
    }
}
