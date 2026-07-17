package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatModel chatModel;

    //constructor for chatModel so spring doesn't confuse b/w OpenAI and Gemini Bean
    public ChatService(@Qualifier("googleGenAiChatModel") ChatModel chatModel)
    {
        this.chatModel = chatModel;
    }

//    public String getResponse(String prompt)
//    {
//        return chatModel.call(prompt);
//    }

    public String getResponseOptions(String prompt)
    {
        ChatResponse response=chatModel.call(
                new Prompt(
                        prompt,
                        GoogleGenAiChatOptions.builder()
                                //Temprature can be set from 0-2, lower value will give focused and deterministic
                                //response like 0.2 and higher value will give more random responses like 0.8
                                .temperature(0.4)
                                .model("gemini-3.1-flash-lite")
                                .build()
                )
        );
        return response.getResult().getOutput().getText();
    }


}
