package org.example.utils;

import org.example.dto.ExpenseDto;
import org.example.entity.ExpenseEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpenseEntityFromDto(ExpenseDto dto, @MappingTarget ExpenseEntity entity);
}
