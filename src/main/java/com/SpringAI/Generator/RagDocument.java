package com.SpringAI.Generator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class RagDocument {

    @Id
    private String id;

    private String userId;

    private String filename;

    private int chunkCount;

    private Instant createdAt = Instant.now();
}
