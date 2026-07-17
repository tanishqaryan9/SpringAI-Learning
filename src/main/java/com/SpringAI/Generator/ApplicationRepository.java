package com.SpringAI.Generator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<UserProfile,Long> {

    UserProfile findByEmail(String username);

    boolean existsByEmail(String email);
}
