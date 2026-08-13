package com.connor.sdk.infrastructure.zhipu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Connor
 * @description
 * @create 2026/8/14 0:11
 */
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    private Thinking thinking;

    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private float temperature;

    public static class Thinking {

        private String type;

        public Thinking() {
        }

        public Thinking(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    //私有化构造方法
    private ChatCompletionRequest(Builder builder) {
        this.model = builder.model;
        this.messages = builder.messages;
        this.thinking = builder.thinking;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
    }

    // 创建 Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String model;
        private List<ChatMessage> messages;
        private Thinking thinking;
        private Integer maxTokens;
        private float temperature;

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder messages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public Builder thinking(Thinking thinking) {
            this.thinking = thinking;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        // 创建最终的 ChatRequest
        public ChatCompletionRequest build() {
            return new ChatCompletionRequest(this);
        }
    }





    // Jackson 序列化需要 Getter
    public String getModel() {
        return model;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public Thinking getThinking() {
        return thinking;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public float getTemperature() {
        return temperature;
    }
}
