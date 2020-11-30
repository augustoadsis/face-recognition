package com.augusto.facerecognition;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.net.URI;

public class Azure {

    public static final String KEY = "YOUR_KEY";
    public static final String ENDPOINT = "https://YOURAZUREENDPOINT/face/v1.0";

    public static final String IMAGE1 = "https://statig0.akamaized.net/bancodeimagens/8v/t3/in/8vt3incliwwx1k9q80hfj1p88.jpg";
    public static final String IMAGE2 = "https://www.jornalcontabil.com.br/wp-content/uploads/2018/02/silvio.jpg";

    public static void main(String[] args) {
        verify();
    }

    public static String detect(String url) {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder(ENDPOINT + "/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("recognitionModel", "recognition_03");
            builder.setParameter("returnRecognitionModel", "false");
            builder.setParameter("detectionModel", "detection_02");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", KEY);


            // Request body
            StringEntity reqEntity = new StringEntity("""
                    {
                            "url": "%s"
                    }
                    """.formatted(url));
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            String body = EntityUtils.toString(response.getEntity());

            if (body != null) {
                System.out.println(body);
            }

            return body;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void verify() {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder(ENDPOINT + "/verify");


            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", KEY);

            String response = detect(IMAGE1);
            String responseVerify = detect(IMAGE2);

            JSONObject face1 = new JSONArray(response).getJSONObject(0);
            JSONObject face2 = new JSONArray(responseVerify).getJSONObject(0);

            // Request body
            StringEntity reqEntity = new StringEntity("""
                    {
                        "faceId1": "%s",
                        "faceId2": "%s",
                    }
                    """.formatted(face1.optString("faceId"), face2.optString("faceId")));
            request.setEntity(reqEntity);

            HttpResponse r = httpclient.execute(request);
            HttpEntity entity = r.getEntity();

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

