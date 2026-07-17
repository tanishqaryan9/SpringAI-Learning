package com.SpringAI.Generator;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ImageService {

    private static final String BASE_URL = "https://image.pollinations.ai/prompt/";
    private final RestClient restClient = RestClient.create();

    public byte[] generateImage(String prompt) {
        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8)
                .replace("+", "%20");

        byte[] imageBytes = restClient.get()
                .uri(BASE_URL + encodedPrompt)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RuntimeException("Pollinations request failed: " + res.getStatusCode());
                })
                .body(byte[].class);

        if (imageBytes == null || imageBytes.length == 0) {
            throw new RuntimeException("No image returned for prompt: " + prompt);
        }

        return imageBytes;
    }
}