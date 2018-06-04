package utilTest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author: zijing
 * @date: 2018/6/4 11:34
 * @description:
 */
public class HTTPClientTest {

    @Test
    public void HTTPClientTest() throws IOException {

        HttpHost proxy = new HttpHost("127.0.0.1", 1080, "http");

        //把代理设置到请求配置
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();

        //创建一个httpclient对象

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();

//创建一个GET对象

        HttpGet get = new HttpGet("https://www.google.com/");

//执行请求

        CloseableHttpResponse response = httpClient.execute(get);

//取响应的结果

        int statusCode = response.getStatusLine().getStatusCode();

        System.out.println(statusCode);

        HttpEntity entity = response.getEntity();

        String string = EntityUtils.toString(entity, "utf-8");

        System.out.println(string);

//关闭httpclient

        response.close();

        httpClient.close();
    }

    @Test
    public void okHTTP() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)))
                .build();//创建OkHttpClient对象
        Request request = new Request.Builder()
                .url("http://www.google.com")//请求接口。如果需要传参拼接到接口后面。
                .build();//创建Request 对象
        Response response= client.newCall(request).execute();//得到Response 对象
        System.out.println(response.body().string());

    }
}
