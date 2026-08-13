package com.connor.sdk.infrastructure.zhipu.dto;

import java.util.List;

/**
 * @author Connor
 * @description
 * @create 2026/8/14 1:18
 */
public class ChatCompletionResponse {

    // 本次请求的唯一标识
    private String id;

    // 请求唯一标识
    private String request_id;

    // 请求创建时间，Unix 时间戳
    private Long created;

    // 实际使用的模型名称
    private String model;

    // 模型返回的结果列表
    private List<Choice> choices;

    // Token 使用统计
    private Usage usage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public static class Choice {

        // 当前结果在 choices 中的下标
        private Integer index;

        // 模型返回的消息
        private Message message;

        // stop、length 等结束原因
        private String finish_reason;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }

    public static class Message {

        // 通常是 assistant
        private String role;

        // 大模型最终回答的内容
        private String content;

        // 开启思考模式时可能返回的思考内容
        private String reasoning_content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReasoning_content() {
            return reasoning_content;
        }

        public void setReasoning_content(String reasoning_content) {
            this.reasoning_content = reasoning_content;
        }
    }

    public static class Usage {

        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;

        public Integer getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(Integer prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public Integer getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(Integer completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public Integer getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(Integer total_tokens) {
            this.total_tokens = total_tokens;
        }
    }
}
