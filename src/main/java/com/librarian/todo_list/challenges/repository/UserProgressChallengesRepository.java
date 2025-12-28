package com.librarian.todo_list.challenges.repository;

import com.librarian.todo_list.challenges.entity.Challenges;
import com.librarian.todo_list.challenges.entity.UserProgressChallenges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressChallengesRepository extends JpaRepository<UserProgressChallenges, Long> {
}
