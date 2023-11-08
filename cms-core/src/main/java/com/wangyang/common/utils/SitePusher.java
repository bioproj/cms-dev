package com.wangyang.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class SitePusher {
    private static  String API = "http://data.zz.baidu.com/urls";
    private static  String site ;//= "https://www.bioinfo.online";
    private static   String token ;//= "RGbsR3A09kEW9cWU";

    @Value("${pusher.baidu.site}")
    public  void setSite(String site) {
        SitePusher.site = site;
    }
    @Value("${pusher.baidu.token}")
    public  void setToken(String token) {
        SitePusher.token = token;
    }

    public static String push(String... urls){

        try {
//            String[] urls = new String[] {
//                    "https://www.example.com/article/123",
//                    "https://www.example.com/article/124",
//                    "https://www.example.com/article/125"
//            };

            // 构造API请求URL及参数
//            String apiUrl = String.format( API,site, token);
            StringBuilder sb = new StringBuilder();
            for (String url : urls) {
                sb.append(site+url).append("\n");
            }
            byte[] postData = sb.toString().getBytes("UTF-8");

//            // 发送HTTP POST请求，并读取响应结果
//            URL url = new URL(apiUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "text/plain");
//            conn.setRequestProperty("Connection", "close");
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postData);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//            reader.close();







            String api = "http://data.zz.baidu.com/urls?site="+site+"&token="+token;
            URL obj = new URL(api);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //添加请求头
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "text/plain");

//            String postData = url;

            //发送POST请求
            con.setDoOutput(true);
            con.getOutputStream().write(postData);
//            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//            wr.write(postData);
//            wr.get
//            wr.flush();

            //获取响应码
            int responseCode = con.getResponseCode();
//            System.out.println("\nSending 'POST' request to URL : " + api);
//            System.out.println("Post Data : " + postData);
//            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info(response.toString());
            return response.toString();
            //打印响应结果
//            System.out.println();
        } catch (IOException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
            return null;
        }

    }
}
