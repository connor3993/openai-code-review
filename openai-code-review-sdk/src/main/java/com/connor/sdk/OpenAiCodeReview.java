package com.connor.sdk;

import com.alibaba.fastjson2.JSON;
import com.connor.sdk.domian.Message;
import com.connor.sdk.infrastructure.zhipu.client.ZhipuClient;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionRequest;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionResponse;
import com.connor.sdk.infrastructure.zhipu.dto.ChatMessage;
import com.connor.sdk.utils.WXAccessTokenUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Connor
 * @description
 * @create 2026/7/10 14:04
 */
public class OpenAiCodeReview {

    static String token = "960792da84824b6aa1db76c6194fc655.BTyIcSmZtjYyFQlp";


    public static void main(String[] args) throws Exception {
        System.out.println("openai代码评审，测试执行");

        String token = System.getenv("GITHUB_TOKEN");
        if(token == null || token.isEmpty()){
            throw new RuntimeException("token is null");
        }

        //1.代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git","diff","HEAD~1","HEAD");

        processBuilder.directory(new File("./"));

        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;


        StringBuilder diffCode = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            diffCode.append(line).append(System.lineSeparator());
        }

        int exitCode = process.waitFor();

        System.out.println("exit with code: " + exitCode);

        System.out.println("代码评审:" + diffCode.toString());


        //2.chatgpt 代码评审
        String log = codeReview(diffCode.toString());
        System.out.println("评审结果:" + log);

        //3.写入日志
        String logUrl = writeLog(token, log);
        System.out.println("评审日志地址：" + logUrl);

        //4.推送到微信消息
    }

    private static void pushMessage(String logUrl) {
        String accessToken = WXAccessTokenUtils.getAccessToken();
        System.out.println(accessToken);

        Message message = new Message();
        message.put("project", "code-review");
        message.put("review", logUrl);
        message.setUrl(logUrl);
        message.setTemplate_id("PeoMZesdC2KZfWtPT-eEOOz1f7a7XMCNKUifRJMH8c4");

        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);
        sendPostRequest(url, JSON.toJSONString(message));
    }


    public static String codeReview(String diffCode) throws Exception {
        //初始化zhipu client
        ZhipuClient client = ZhipuClient.builder().apiKey("4bb9d364548246a4b9fe292292dc532e.dCTeVFnDop5aK7Pb").build();

        //构建message list,传入diffcode
        List<ChatMessage> messages = List.of(
                ChatMessage.builder()
                        .role("user")
                        .content("你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为:" + diffCode)
                        .build()
        );
        //配置client
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("glm-4.7-flash")
                .messages(messages)
                .thinking(new ChatCompletionRequest.Thinking("disabled"))
                .maxTokens(65536)
                .temperature(1.0f)
                .build();

        //发送chat请求
        ChatCompletionResponse response = client.chat(request);
        //处理结果
        return response.getChoices().get(0).getMessage().getContent();
    }


    public static String writeLog(String token, String log) throws Exception {
            Git git = Git.cloneRepository()
                    .setURI("https://github.com/connor3993/openai-code-review-log")
                    .setDirectory(new File("repo"))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                    .call();

            String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File dateFolder = new File("repo/" + dateFolderName);
            if (!dateFolder.exists()) {
                dateFolder.mkdirs();
            }

            String fileName = generateRandomString(12) + ".md";
            File newFile = new File(dateFolder, fileName);
            try (FileWriter writer = new FileWriter(newFile)) {
                writer.write(log);
            }

            git.add().addFilepattern(dateFolderName + "/" + fileName).call();
            git.commit().setMessage("Add new file").call();
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                    .call();

            return "https://github.com/connor3993/openai-code-review-log/blob/master/" + dateFolderName + "/" + fileName;
        }

        private static String generateRandomString(int length) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            return sb.toString();
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


}
