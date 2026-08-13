package com.connor.sdk.infrastructure.zhipu.client;

import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionRequest;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
/**
 * @author Connor
 * @description
 * @create 2026/8/13 22:45
 */
public class ZhipuClient {

    private final String baseUrl;
    private final String apiKey;
    // 声明 WebClient 对象
    private final WebClient webClient;

    private ZhipuClient(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        // 创建 WebClient 实例
        this.webClient = WebClient.builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
        private String apiKey;



        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public ZhipuClient build() {
            return new ZhipuClient(this);
        }
    }

    public ChatCompletionResponse chat(ChatCompletionRequest request) {
        //1.将request->jackson->json

        //2.http post request

        //3.将http返回结果存入ChatCompletionResponse

        return webClient.post()
                // 请求地址
                .uri(baseUrl)

                // 请求头
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)

                // 请求体
                .bodyValue(request)

                // 发送请求
                .retrieve()

                // 将返回 JSON 转换成响应对象
                .bodyToMono(ChatCompletionResponse.class)

                // 当前先使用同步方式拿到结果
                .block();
    }

}
