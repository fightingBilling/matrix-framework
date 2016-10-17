package org.matrix.framework.core.http;

import com.google.common.io.BaseEncoding;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pktczwd on 2016/9/21.
 */
public class Main {
    private static final String key = "test";
    private static final String secret = "NmRkMTlj";


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        // write your code here
        HttpClient client = HttpClients.createDefault();

        StringBuilder sb = new StringBuilder(50);
        sb.append(secret);
        sb.append("http-methodget");
        sb.append("http-request-path");
        sb.append("/api/v1/users/lattices/recommend");
        sb.append("mobile15881808180");
        sb.append(secret);

        MessageDigest md5 = MessageDigest.getInstance("MD5");

        String infoToSign = URLEncoder.encode(sb.toString(), "utf-8");
        byte[] i = md5.digest(infoToSign.getBytes("utf-8"));
        String hex = toHexString(i);
        String s = BaseEncoding.base64().encode(hex.getBytes("utf-8"));

        HttpGet get = new HttpGet("http://test.data.sudiyi.cn/api/v1/users/lattices/recommend?mobile=15881808180");

        get.addHeader("Authorization", "Data test:" + s + "\n");

        HttpResponse resp = client.execute(get);

        BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        String line = reader.readLine();
        while (line != null) {
            System.out.print(line);
            line = reader.readLine();
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
