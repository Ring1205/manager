package com.zxycloud.common.utils.netWork;

import com.zxycloud.common.base.BaseBean;
import com.zxycloud.common.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leiming
 * @date 2018/12/20.
 */
public class ApiRequest<T extends BaseBean> {
    private String action;

    private int apiType = NetUtils.BUSINESS;

    private int requestType = 0;

    private Object tag;

    private Map<String, Object> requestParams;

    private Object requestBody;

    private Class<T> resultClazz;

    public ApiRequest(String action, Class<T> resultClazz) {
        this.action = action;
        this.resultClazz = resultClazz;
    }

    public ApiRequest setApiType(@NetUtils.NetApiType int apiType) {
        this.apiType = apiType;
        return this;
    }

    public ApiRequest setRequestType(@NetUtils.NetRequestType int requestType) {
        if (requestType != 0) {
            throw new IllegalStateException("Request type can't repeat settings");
        }
        this.requestType = requestType;
        return this;
    }

    public ApiRequest setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public String getAction() {
        return action;
    }

    public int getApiType() {
        return apiType;
    }

    public int getRequestType() {
        return requestType == 0 ? NetUtils.GET : requestType;
    }

    public Object getTag() {
        return tag;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public Class<T> getResultClazz() {
        return resultClazz;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public ApiRequest setRequestParams(String key, Object value) {
        if (CommonUtils.isEmpty(requestParams)) {
            requestParams = new HashMap<>();
        }
        requestParams.put(key, value);
        return this;
    }

    /**
     * 防止多次添加body导致异常
     *
     * @param body post信息体
     */
    public void setRequestBody(Object body) {
        if (requestType == NetUtils.GET) {
            throw new IllegalStateException("GET can't support body, must be POST");
        }
        requestBody = body;
    }
}
