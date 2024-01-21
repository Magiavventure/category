package it.magiavventure.category.mapper;

import it.magiavventure.category.model.Category;
import it.magiavventure.mongo.entity.ECategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category map(ECategory eUser);
}
