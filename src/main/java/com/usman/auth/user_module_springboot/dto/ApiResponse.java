package com.usman.auth.user_module_springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> extends HashMap<String, Object> {

    public ApiResponse() {
        // empty constructor for serialization
    }

    public ApiResponse(boolean success, String message) {
        put("success", success);
        put("message", message);
    }

    public ApiResponse(boolean success, String message, Object data) {
        put("success", success);
        put("message", message);
        if (data != null) {
            put("data", data);
        }
    }

    public void setSuccess(boolean success) {
        put("success", success);
    }

    public void setMessage(String message) {
        put("message", message);
    }

    public void setData(Object data) {
        put("data", data);
    }

    public void addField(String key, Object value) {
        put(key, value);
    }
}
