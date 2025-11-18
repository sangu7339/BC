package com.venturebiz.in.BusinessConnect.response;
public class ResponseBuilder {

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<?> success(String message) {
        return ApiResponse.builder()
                .status("success")
                .message(message)
                .data(null)
                .build();
    }

    public static ApiResponse<?> error(String message) {
        return ApiResponse.builder()
                .status("error")
                .message(message)
                .data(null)
                .build();
    }
}
