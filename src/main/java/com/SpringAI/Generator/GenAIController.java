package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class GenAIController {

    private final ChatService chatService;
    private final ImageService imageService;
    private final RecipeService recipeService;
    private final AudioService audioService;


    @GetMapping(value="/ask-ai", produces = "text/plain")
    public String getResponse(@RequestParam String prompt)
    {
        return chatService.getResponseOptions(prompt);
    }

    @GetMapping(value = "/generate-image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateImage(@RequestParam String prompt) {
        return imageService.generateImage(prompt);
    }

    @GetMapping(value="/recipe-creator", produces = "text/plain")
    public String createRecipe(@RequestParam String ingredients, @RequestParam(defaultValue = "any") String cuisine, @RequestParam(defaultValue = "") String dietaryRestrictions)
    {
        return recipeService.createRecipe(ingredients,cuisine,dietaryRestrictions);
    }

    @PostMapping(value="/audio", consumes = "multipart/form-data")
    public String transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        return audioService.transcribe(file);
    }

    @GetMapping("/speak")
    public ResponseEntity<byte[]> speak(@RequestParam String text) throws IOException {

        InputStream stream=audioService.textToSpeech(text);

        byte[] audio=stream.readAllBytes();
        return ResponseEntity.ok().header("Content-Type", "audio/mpeg").body(audio);
    }
}
