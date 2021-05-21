package com.azizi.app.store.mapper;

import com.azizi.app.store.dto.AppDto;
import com.azizi.app.store.entity.App;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface AppMapper {

    AppDto toDto(App app);

    @Mapping(target = "publishedAt", ignore = true)
    App toEntity(AppDto appDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    App toEntity(AppDto appDto, @MappingTarget App app);

}
