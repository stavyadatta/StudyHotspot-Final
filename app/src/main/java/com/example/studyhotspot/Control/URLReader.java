package com.example.studyhotspot.Control;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * URLReader is class that provides API querying services to other classes.
 * It takes a URL, and returns the corresponding data in the desired format.
 */
public class URLReader {
    /**
     * URL2String takes a URL, accesses the URL, collect the content, and then return it in String format.
     * @param urlString A website URL
     * @return corresponding data as a string
     * @throws Exception
     */
    public static String URL2String(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * URL2JSON takes a URL, accesses the URL, collect the content, and then return it in JSON format.
     * @param urlString A website URL
     * @return corresponding data as a JSONObject
     * @throws Exception
     */

    public static JSONObject URL2JSON(String urlString) throws Exception {
        String jsonstring = URL2String(urlString);
        JSONObject returnObject = new JSONObject(jsonstring);
        return returnObject;
    }
}
