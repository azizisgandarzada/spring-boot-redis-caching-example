package com.azizi.app.store.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppDto implements Serializable {

    private static final long serialVersionUID = 2456772146950251807L;

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String author;

    @NotBlank
    private String description;

    private CategoryDto category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAt;

}
