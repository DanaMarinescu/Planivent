package com.event.eventapp.repository;

import com.event.eventapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("SELECT u.id as id, u.name as name, u.email as email, u.phone as phone, " +
            "r.role as role " +
            "FROM User u " +
            "JOIN u.role r " +
            "WHERE u.id = :userId")
    UserProjection findUserProjectionById(@Param("userId") Long userId);
}
