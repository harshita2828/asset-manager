package com.example.ams.datamodels.form.response;

public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;
    public APIResponse(boolean success, String message, T data) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
