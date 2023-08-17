package ink.akira.spring.web;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
