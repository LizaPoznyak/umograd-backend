package com.umograd.analytic.repository.analytic;

import com.umograd.analytic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnalyticsRepository extends JpaRepository<UserEntity, Long> {
}
