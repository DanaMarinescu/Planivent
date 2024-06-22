package com.event.eventapp.repository;

import com.event.eventapp.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUserId(Long userId);

    Optional<UserTask> findByUserIdAndTaskName(Long userId, String taskName);

    boolean existsByTaskName(String taskName);

    Optional<UserTask> findByUuidAndUserId(String uuid, Long userId);

    Optional<UserTask> findByUuid(String uuid);

    void deleteByUuid(String uuid);
}

