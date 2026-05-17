package com.umograd.analytic.service;

import com.umograd.analytic.dto.AchievementGrantResponse;
import com.umograd.analytic.dto.AchievementResponse;

import java.util.List;

public interface AchievementService {

    List<AchievementGrantResponse> checkAndGrant(Long childId);

    List<Long> getEarnedAchievementIds(Long childId);

    List<AchievementResponse> getAchievements();
}
