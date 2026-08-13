package com.connor.sdk.infrastructure.zhipu.dto;

/**
 * @author Connor
 * @description
 * @create 2026/8/14 0:38
 */
public class ChatMessage {
    private String role;
    private String content;

    private ChatMessage(Builder builder) {
        this.content = builder.content;
        this.role = builder.role;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String content;
        private String role;

        public Builder content(String content) {
            this.content = content;
            return this;
        }
        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }

    }



    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
