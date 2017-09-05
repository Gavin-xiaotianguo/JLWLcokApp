package com.example.gxt.jlwlcokapp.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JLW on 2017/8/14.
 */

public class WebServicePost {
    private static String IP = "192.168.1.131:8080";

    // 通过Get方式获取HTTP服务器数据
    public static String executeHttpPost(String username, String userMAC, String url ) {
        String result = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader in = null;

        try {
            // 用户名 密码
            // URL 地址
            String path = "http://" + IP + "/TomcatTest/"+url;
            path = path + "?userid=" + username + "&userMAC=" + userMAC;
            conn = (HttpURLConnection) new URL( path ).openConnection();
            conn.setConnectTimeout( 3000 ); // 设置超时时间
            conn.setReadTimeout( 3000 );
            conn.setDoInput( true );
            conn.setDoOutput( true );
            conn.setRequestMethod( "POST" ); // 设置获取信息方式
            conn.setRequestProperty( "Charset", "UTF-8" );
            DataOutputStream dop = new DataOutputStream(
                    conn.getOutputStream() );
            dop.writeBytes( "token=alexzhou" );
            dop.flush();
            dop.close();

            in = new InputStreamReader( conn.getInputStream() );
            BufferedReader bufferedReader = new BufferedReader( in );
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append( line );
            }
            result = strBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
// 将输入流转化为 String 型
   /* private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}*/
