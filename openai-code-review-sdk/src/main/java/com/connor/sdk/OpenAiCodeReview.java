package com.connor.sdk;

import com.alibaba.fastjson2.JSON;
import com.connor.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.connor.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
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

        //3.写入日志
        String logUrl = writeLog(token, log);
        System.out.println("评审日志地址：" + logUrl);
    }


    public static String codeReview(String diffCode) throws Exception {
        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);


        ChatCompletionRequestDTO request = new ChatCompletionRequestDTO();
        request.setModel("glm-4.5-flash");
        request.setMessages(List.of(
                new ChatCompletionRequestDTO.Prompt(
                        "user",
                        "你是一个高级编程架构师，请根据 git diff 记录进行代码评审。"
                ),
                new ChatCompletionRequestDTO.Prompt("user", diffCode)
        ));
        String requestJson = JSON.toJSONString(request);


        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }


        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        StringBuilder content = new StringBuilder();
        InputStream responseStream = responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
        if (responseStream != null) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
            }
        }
        connection.disconnect();

        if (responseCode < 200 || responseCode >= 300) {
            throw new IllegalStateException(
                    "模型接口调用失败，HTTP " + responseCode + "：" + content
            );
        }

        System.out.println("评审结果：" + content.toString());

        ChatCompletionSyncResponseDTO response = JSON.parseObject(
                content.toString(),
                ChatCompletionSyncResponseDTO.class
        );
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
}
