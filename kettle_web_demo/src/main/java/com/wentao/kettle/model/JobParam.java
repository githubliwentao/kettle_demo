package com.wentao.kettle.model;

import java.io.Serializable;
import java.util.Map;

/**
 * job参数
 *
 * @author
 * @since 1.0
 */
public class JobParam implements Serializable {

    /**
     * job id
     */
    private Long id;

    /**
     * job history id
     */
    private Long historyId;

    /**
     * job path（kjb only）
     */
    private String path;

    /**
     * 变量，可在job中获取到
     */
    private Map<String, String> variables;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}
