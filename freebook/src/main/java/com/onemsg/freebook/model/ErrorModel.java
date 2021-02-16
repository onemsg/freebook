package com.onemsg.freebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorModel {
    
    private String code;
    private String message;

    public static ErrorModel of(String code, String message){
        return new ErrorModel(code, message);
    }
}
