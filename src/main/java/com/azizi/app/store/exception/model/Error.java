package com.azizi.app.store.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Error {

    private String timestamp;
    private String error;
    private String message;
    private String path;

}
