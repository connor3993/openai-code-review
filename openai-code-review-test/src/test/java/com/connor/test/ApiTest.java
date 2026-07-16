package com.connor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
