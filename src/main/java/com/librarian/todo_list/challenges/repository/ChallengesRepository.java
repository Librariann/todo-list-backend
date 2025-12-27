package com.librarian.todo_list.challenges.repository;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.rewards.entity.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengesRepository extends JpaRepository<Challenges, Long> {
   /*
    * 보상목록 이름으로 조회
    */
    boolean existsByName(String name);
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
    //GetList Active true
    List<Challenges> findByIsActiveTrue();

    //GetOne Active true
    Optional<Challenges> findByIdAndIsActiveTrue(Long id);
}
