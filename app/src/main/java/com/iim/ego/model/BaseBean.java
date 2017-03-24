package com.iim.ego.model;

/**
 * 数据请求基础类
 * Created by Hoyn on 17/3/24.
 */

public class BaseBean {
    //状态码
    int code;
    //返回操作信息
    String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
