package com.azizi.app.store.mapper;

import com.azizi.app.store.dto.UserDto;
import com.azizi.app.store.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDto toDto(User user);

}
