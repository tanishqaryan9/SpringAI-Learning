package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/ai/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagIngestionService ingestion;
    private final RagChatService chat;
    private final RagDocumentRepository documentRepository;
    private final VectorStore vectorStore;

    @GetMapping("/documents")
    public List<RagDocument> list(Authentication auth) {
        return documentRepository.findByUserIdOrderByCreatedAtDesc(auth.getName());
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RagIngestionService.IngestResult upload(@RequestParam("file") MultipartFile file, Authentication auth) throws IOException
    {
        validate(file);
        return ingestion.ingest(file, auth.getName());
    }

    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> body, Authentication auth) {
        return Map.of("answer", chat.ask(body.get("question"), auth.getName()));
    }

    @DeleteMapping("/documents/{docId}")
    public void delete(@PathVariable String docId, Authentication auth) {
        ingestion.delete(docId, auth.getName());
    }

    @GetMapping("/debug")
    public List<Map<String, String>> debug(@RequestParam String q, Authentication auth) {
        var results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(q)
                .topK(5)
                .similarityThreshold(0.0)
                .filterExpression(new FilterExpressionBuilder().eq("userId", auth.getName()).build())
                .build());
        return results.stream().map(d -> Map.of(
                "score", String.valueOf(d.getScore()),
                "text", d.getText().substring(0, Math.min(150, d.getText().length())),
                "userId", String.valueOf(d.getMetadata().get("userId"))
        )).toList();
    }

    private void validate(MultipartFile f)
    {
        String name = f.getOriginalFilename() == null ? "" : f.getOriginalFilename().toLowerCase();

        boolean ok = name.endsWith(".pdf") || name.endsWith(".txt") || name.endsWith(".md");

        if (f.isEmpty() || !ok) throw new IllegalArgumentException("Upload a non-empty .pdf, .txt or .md file");
    }
}
