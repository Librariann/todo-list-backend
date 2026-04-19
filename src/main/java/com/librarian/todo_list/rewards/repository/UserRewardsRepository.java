package com.librarian.todo_list.rewards.repository;

import com.librarian.todo_list.rewards.entity.Rewards;
import com.librarian.todo_list.rewards.entity.UserReward;
import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRewardsRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUser(User user);
}
