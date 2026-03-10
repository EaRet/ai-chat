package com.zj.aichat.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Bean
    public ContentRetriever ContentRetriever() {
        //读取文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
        //定义文档解析器
        DocumentByParagraphSplitter documentSplitter =
                new DocumentByParagraphSplitter(1000, 200);

        //定义向量规则
        EmbeddingStoreIngestor build = EmbeddingStoreIngestor.builder()
                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .textSegmentTransformer(segment -> TextSegment.from(segment.metadata().getString("file_name")
                        +"\n"+ segment.text(), segment.metadata()))
                .build();

        //喂文档解析
        build.ingest(documents);

        //定义内容检索
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(5)
                .minScore(0.75)
                .build();
    }
}
