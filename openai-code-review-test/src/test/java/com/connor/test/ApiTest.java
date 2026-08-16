package com.connor.test;

import com.alibaba.fastjson2.JSON;
import com.connor.sdk.infrastructure.zhipu.client.ZhipuClient;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionRequest;
import com.connor.sdk.infrastructure.zhipu.dto.ChatCompletionResponse;
import com.connor.sdk.infrastructure.zhipu.dto.ChatMessage;
import com.connor.sdk.utils.WXAccessTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Connor
 * @description JUnit 5 + Spring Boot 3 测试样例
 */
@Slf4j
@SpringBootTest
public class ApiTest {

    @Test
    public void test() {
        log.info("测试 Spring Boot 3 + JUnit 5");
    }

}
