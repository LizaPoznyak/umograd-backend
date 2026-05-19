package com.umograd.analytic.mapper;

import com.umograd.analytic.dto.ParentAgeLimitResponse;
import com.umograd.analytic.entity.ParentAgeLimitEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LimitMapper {

    ParentAgeLimitResponse toDto(ParentAgeLimitEntity entity);

    List<ParentAgeLimitResponse> toListDto(List<ParentAgeLimitEntity> list);
}
