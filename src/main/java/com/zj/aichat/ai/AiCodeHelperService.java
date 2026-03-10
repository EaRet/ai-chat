package com.zj.aichat.ai;

import com.zj.aichat.ai.guardrail.SafeInputGuardrail;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import java.util.List;

//@AiService
@InputGuardrails({ SafeInputGuardrail.class })
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(UserMessage message);

    //结构化输出 JSON/JSON Scheme
    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatGetReport(String message);

    record Report(String name, List<String> suggestList){};

    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String message);

    @SystemMessage(fromResource = "system-prompt.txt")
    Flux<String> chatStream(@MemoryId int memoryId, @dev.langchain4j.service.UserMessage String message);
}
