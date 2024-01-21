package com.hotcoin.activity.model;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.email.controller
 * @ClassName: EmailController
 * @Author: hf
 * @Description:
 * @Date: 2019/5/30 18:15
 * @Version: 1.0
 */

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 2120267584344923858L;
    private Integer status = 200;
    private String message = null;
    private T data = null;
    private JSONObject page = null;

    public Result() {
    }

    /**
     * 失败时候的调用
     *
     * @param message
     * @param <T>     t
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return new Result<T>(message);
    }
    /**
     * 成功时候的调用
     *
     * @param data data
     * @param <T>  t
     * @return Result
     */
    /**
     * 失败的构造函数
     *
     * @param message
     */
    private Result(String message) {
        if (!StringUtils.isEmpty(message)) {
            this.status = 400;
            this.message = message;
            this.data = null;
        }
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<T>(data, message);
    }

    /**
     * 成功的构造函数
     *
     * @param data data
     */
    public Result(T data, String message) {
        // 默认200是成功
        this.status = 200;
        this.message = message;
        this.data = data;
    }

    public Result(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public JSONObject getPage() {
        return page;
    }

    public void setPage(JSONObject page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public void savePage(int currentPage, int pageSize, long totalPage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("pageSize", pageSize);
        jsonObject.put("totalPage", totalPage);
        this.setPage(jsonObject);
    }
}


