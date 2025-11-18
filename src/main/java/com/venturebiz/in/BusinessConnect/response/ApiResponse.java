package com.venturebiz.in.BusinessConnect.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private String status;   
    private String message;  
    private T data;         
}
