package com.mju.dearseoul.domain.letter.repository;

import com.mju.dearseoul.domain.letter.entity.ExperienceLetter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ExperienceLetterRepository extends JpaRepository<ExperienceLetter, String> {
    List<ExperienceLetter> findAllByOrderByCreatedAtDescIdAsc(Pageable pageable);
    List<ExperienceLetter> findByPlaceIdInAndOwnerNotOrderByCreatedAtDescIdAsc(List<String> places, String owner, Pageable pageable);
    List<ExperienceLetter> findByPlaceIdInAndOwnerNotAndIdNotInOrderByCreatedAtDescIdAsc(
            List<String> places, String owner, Collection<String> excludedIds, Pageable pageable);
    Optional<ExperienceLetter> findByRequestKey(String requestKey);
    Optional<ExperienceLetter> findByReviewId(String reviewId);
}
