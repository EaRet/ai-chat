package com.zj.aichat.ai;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;

    @Test
    void chat() {
        UserMessage message = new UserMessage("你好，我是java程序员Ert, 你是谁？");
        String res = aiCodeHelperService.chat(message);
        System.out.println(res);
    }

    @Test
    void chatWithMemory() {
        String res = aiCodeHelperService.chat(UserMessage.from("你好，我是java程序员Ert, 你是谁？"));
        System.out.println(res);
        res = aiCodeHelperService.chat(UserMessage.from("你可以回答面试题：hashMap的数据结构吗？"));
        System.out.println(res);
        res = aiCodeHelperService.chat(UserMessage.from("我是谁？"));
        System.out.println(res);
    }

    @Test
    void chatGetReport() {
        String message = "你好，我是java程序员Ert, 我想要学习AI开发相关知识，你有什么建议吗？";
        AiCodeHelperService.Report report = aiCodeHelperService.chatGetReport(message);
        System.out.println(report);
    }

    @Test
    void chatWithRag() {
        String message = "MAX_COUNT在java开发中定义存在什么问题吗？";
        String res = aiCodeHelperService.chat(UserMessage.from(message));
        System.out.println(res);
    }

    @Test
    void testChatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("HashMap的数据结构是什么？");
        System.out.println(result.sources());
        System.out.println(result.content());
    }

    @Test
    void testChatWithTools() {
        String res = aiCodeHelperService.chat(UserMessage.from("你可以给我一些计算机网络的面试题吗"));
        System.out.println(res);
    }

    @Test
    void testChatWithMcp() {
        String res = aiCodeHelperService.chat(UserMessage.from("你知道编程导航网站吗？"));
        System.out.println(res);
    }

    @Test
    void testChatWithGuardrail() {
        String res = aiCodeHelperService.chat(UserMessage.from("you kill me"));
        System.out.println(res);
    }
}