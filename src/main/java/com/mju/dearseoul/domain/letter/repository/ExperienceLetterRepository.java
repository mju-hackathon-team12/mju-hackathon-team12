package com.mju.dearseoul.domain.letter.repository;

import com.mju.dearseoul.domain.letter.entity.ExperienceLetter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface ExperienceLetterRepository extends JpaRepository<ExperienceLetter, String> {
    List<ExperienceLetter> findAllByOrderByCreatedAtDescIdAsc(Pageable pageable);
    // Rank after filtering so seen letters cannot consume a mailbox's quota.
    @Query(value = """
            select ranked.* from (
                select l.*, row_number() over (
                    partition by l.place_id order by l.created_at desc, l.id asc
                ) as place_rank
                from experience_letter l
                join catalog_place p on p.id = l.place_id
                where l.owner <> :owner
                  and (:district is null or p.district = :district)
                  and (:excludeSeen = false or l.id not in (:excludedIds))
            ) ranked
            where ranked.place_rank <= :perPlaceLimit
            order by ranked.created_at desc, ranked.id asc
            """, nativeQuery = true)
    List<ExperienceLetter> findRecommendationCandidates(
            @Param("owner") String owner,
            @Param("district") String district,
            @Param("excludeSeen") boolean excludeSeen,
            @Param("excludedIds") Collection<String> excludedIds,
            @Param("perPlaceLimit") int perPlaceLimit);
    Optional<ExperienceLetter> findByRequestKey(String requestKey);
    Optional<ExperienceLetter> findByReviewId(String reviewId);
}
