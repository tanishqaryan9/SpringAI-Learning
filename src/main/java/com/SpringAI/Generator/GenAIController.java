package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class GenAIController {

    private final ChatService chatService;
    private final ImageService imageService;
    private final RecipeService recipeService;


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
}
