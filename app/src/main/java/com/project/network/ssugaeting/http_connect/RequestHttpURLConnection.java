
package com.project.network.ssugaeting.http_connect;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Jin on 2018-05-31.
 */

public class RequestHttpURLConnection {

    public String request(String s) {

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.

        StringBuffer sbParams = new StringBuffer();

        sbParams.append("&");
        sbParams.append(s);
        sbParams.append("&");

      /*
        // 보낼 데이터가 없으면 파라미터를 비운다.
        if (_params == null)
            sbParams.append("");
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 $가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String key;
            String value;

            sbParams.append("&");
            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 $를 붙인다.
                if (isAnd)
                    sbParams.append("$");

                sbParams.append(key).append("=").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 $를 붙인다.
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
            sbParams.append("&");
        }
        */



        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         **/
        try {
            URL url = new URL("http://192.168.0.5:1234");
            //http://192.168.43.208:12345
            //http://192.168.0.5:1234
            //http://125.141.90.167:1234
            urlConn = (HttpURLConnection) url.openConnection();

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.

            // [2-1]. urlConn 설정.
            urlConn.setReadTimeout(5000);
            urlConn.setConnectTimeout(5000);
            urlConn.setDefaultUseCaches(false);
            urlConn.setDoInput(true); // 서버에서 읽기 모드 지정
            urlConn.setDoOutput(true); // 서버로 쓰기 모드 지정
            urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.

            // [2-2]. parameter 서버 송신
            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1$pw=123;
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return "UNCONNECTED";

            // [2-4]. 서버 수신
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = br.readLine()) != null) {
                page += line;
            }
            br.close();
            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;

    }
}