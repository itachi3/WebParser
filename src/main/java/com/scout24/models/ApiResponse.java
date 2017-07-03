package com.scout24.models;

public class ApiResponse {

    Object success;

    String error;

    public ApiResponse(Object success, String error) {
        this.success = success;
        this.error = error;
    }

    public ApiResponse(String error) {
        this.error = error;
    }

    public ApiResponse(Object success) {
        this.success = success;
    }

    public Object getSuccess() {
        return success;
    }

    public void setSuccess(Object success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
