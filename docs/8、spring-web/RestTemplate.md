# RestTemplate
RestTemplate 是 Spring 提供的用于访问 Rest 服务的客户端库。它提供了一套接口，然后分别用三种 Java 最常用 Http 连接的库来分别实现这套接口：
* JDK 自带的 HttpURLConnection
* Apache 的 HttpClient
* OKHttp3

这里以使用HttpClient作为Http连接库进行 RestTemplate 的配置。

## 示例
### 1) 引入依赖
```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.12</version>
</dependency>
```
### 2) 调用
```java
public class RestTemplateTest {
    @Test
    public void test() {
        String uri = "http://localhost:8080/hello?name=Tom";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(httpClientFactory);
        String result = restTemplate.getForObject(uri, String.class);
        System.out.println(result);
    }
}
```
结果：
```
{"status":0,"message":"SUCCESS","data":"你好, Tom"}
```