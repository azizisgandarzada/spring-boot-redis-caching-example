package com.azizi.app.store.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {

    private static final long serialVersionUID = 2405172146950251812L;

    private Integer id;
    private String name;
    private String surname;

}
