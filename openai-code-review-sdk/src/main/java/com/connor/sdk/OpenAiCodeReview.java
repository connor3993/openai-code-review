package com.connor.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Connor
 * @description
 * @create 2026/7/10 14:04
 */
public class OpenAiCodeReview {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("测试执行");

        //1.代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git","diff","HEAD","HEAD~1");

        processBuilder.directory(new File("./"));

        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;


        StringBuilder diffCode = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();

        System.out.println("exit with code: " + exitCode);
    }
}
