package com.connor.sdk.test;

import com.alibaba.fastjson2.JSON;
import com.connor.sdk.infrastructure.zhipu.client.ZhipuClient;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionRequest;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionResponse;
import com.connor.sdk.infrastructure.zhipu.dto.ChatMessage;
import com.connor.sdk.utils.WXAccessTokenUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Connor
 * @description
 * @create 2026/8/16 17:22
 */
public class ApiTest {
    @Test
    public void test_client() throws IOException {
        //初始化client
        ZhipuClient client = ZhipuClient.builder().apiKey("4bb9d364548246a4b9fe292292dc532e.dCTeVFnDop5aK7Pb").build();


        String code = "1+1";
        //构建message list
        List<ChatMessage> messages = List.of(
                ChatMessage.builder()
                        .role("user")
                        .content("你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为:" + code)
                        .build()
        );


        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("glm-4.7-flash")
                .messages(messages)
                .thinking(new ChatCompletionRequest.Thinking("disabled"))
                .maxTokens(65536)
                .temperature(1.0f)
                .build();

        ChatCompletionResponse response = client.chat(request);

        String content = response.getChoices().get(0).getMessage().getContent();
        System.out.println(content);

    }

    @Test
    public void test_wx() {
        String accessToken = WXAccessTokenUtils.getAccessToken();
        System.out.println(accessToken);

        Message message = new Message();
        message.put("project","big-market");
        message.put("review","feat: 新加功能");

        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);

        sendPostRequest(url, JSON.toJSONString(message));
    }

    private static void sendPostRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Message {
        private String touser = "o_VAk3OfVfmmMoBBxo_bjBNNF4_c";
        private String template_id = "PeoMZesdC2KZfWtPT-eEOOz1f7a7XMCNKUifRJMH8c4";
        private String url = "https://github.com/connor3993/openai-code-review-log/blob/main/2026-08-16/zHQEKNFDeXoR.md";
        private Map<String, Map<String, String>> data = new HashMap<>();

        public void put(String key, String value) {
            data.put(key, new HashMap<String, String>() {
                {
                    put("value", value);
                }
            });
        }

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Map<String, String>> getData() {
            return data;
        }

        public void setData(Map<String, Map<String, String>> data) {
            this.data = data;
        }
    }
}
