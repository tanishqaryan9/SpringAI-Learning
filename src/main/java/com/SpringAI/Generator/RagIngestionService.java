package com.SpringAI.Generator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RagIngestionService {

    private final VectorStore vectorStore;
    private final RagDocumentRepository documentRepository;

    private final TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
            .withChunkSize(600)
            .build();

    public IngestResult ingest(MultipartFile file, String userId) throws IOException
    {
        String docId = UUID.randomUUID().toString();
        String filename = file.getOriginalFilename();

        Resource resource = new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename() {return filename;}
        };

        List<Document> pages = new TikaDocumentReader(resource).get();
        pages.forEach(d-> d.getMetadata().putAll(Map.of(
                "userId", userId,
                "docId", docId,
                "filename", filename == null? "unknown":filename
        )));

        List<Document> chunks = tokenTextSplitter.apply(pages);
        vectorStore.add(chunks);

        var row = new RagDocument();
        row.setId(docId);
        row.setUserId(userId);
        row.setFilename(filename);
        row.setChunkCount(chunks.size());
        documentRepository.save(row);

        return new IngestResult(docId, filename, chunks.size());
    }

    @Transactional
    public void delete(String docId, String userId)
    {
        var f = new FilterExpressionBuilder();
        vectorStore.delete(f.and(f.eq("userId", userId), f.eq("docId", docId)).build());
        documentRepository.deleteByIdAndUserId(docId, userId);
    }

    public record IngestResult(String docId, String filename, int chunks){}
}
