package com.SpringAI.Generator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagDocumentRepository extends JpaRepository<RagDocument, String> {

    List<RagDocument> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByIdAndUserId(String id, String userId);
}
