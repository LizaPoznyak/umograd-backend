package com.umograd.analytic.mapper;

import com.umograd.analytic.dto.AchievementGrantResponse;
import com.umograd.analytic.entity.AchievementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

    @Mapping(target = "newlyEarned", constant = "true")
    AchievementGrantResponse toGrantResponse(AchievementEntity achievement);
}
