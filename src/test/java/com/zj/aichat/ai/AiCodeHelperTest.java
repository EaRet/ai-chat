package com.zj.aichat.ai;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeHelperTest {

    @Resource
    private AiCodeHelper aiCodeHelper;

    //普通对话
    @Test
    void chat() {
//        String res = aiCodeHelper.chat("你好，我是java程序员Ert");
        String res = aiCodeHelper.chat("你好，你是谁");
        System.out.println(res);
    }

    //多模态对话
    @Test
    void chatWithMessage() {
        UserMessage userMessage = new UserMessage(
                TextContent.from("解析图片"),
                ImageContent.from("https://www.codefather.cn/logo.png")
        );
        String res = aiCodeHelper.chatWithMessage(userMessage);
        System.out.println(res);
    }
}