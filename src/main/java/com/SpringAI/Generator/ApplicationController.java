package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(@RequestBody LoginRequestDTO login)
    {
        String token=applicationService.signin(login);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody LoginRequestDTO register)
    {
        String token=applicationService.register(register);
        return ResponseEntity.ok(Map.of("token",token));
    }
}
