package com.azizi.app.store.dto;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto implements Serializable {

    private static final long serialVersionUID = 2405172146950251807L;

    @NotNull
    private Integer id;

    private String name;

}
