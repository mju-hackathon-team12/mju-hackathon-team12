package com.mju.dearseoul.domain.letter.repository;

import com.mju.dearseoul.domain.letter.entity.LetterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LetterReviewRepository extends JpaRepository<LetterReview, String> {
    Optional<LetterReview> findByIdAndOwner(String id, String owner);
}
