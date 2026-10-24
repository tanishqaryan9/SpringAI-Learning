package com.SpringAI.Generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RagChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagChatService(@Qualifier("googleGenAiChatModel")ChatModel chatModel, VectorStore vectorStore)
    {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel).defaultSystem("""
                    You answer questions using ONLY the provided context.
                    If the context does not contain the answer, say you don't know.
                    """).defaultAdvisors(RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(5)
                        .similarityThreshold(0.2)
                        .build()).build()).build();
    }

    public String ask(String question, String userId)
    {
        var filter = new FilterExpressionBuilder().eq("userId",userId).build();
        return chatClient.prompt()
                .user(question)
                .advisors(a-> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, filter))
                .call().content();
    }
}
