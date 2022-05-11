package com.andycoder.tools.dbDiff.tablemeta.mysql;

/**
 * mysql 列定义
 */
public class MysqlColumnMeta {
    /**
     * 字段
     */
    private String field;
    /**
     * 类型
     */
    private String type;
    /**
     * 字段长度
     */
    private Integer length;
    /**
     * 是否为空
     */
    private String nullFlag;
    /**
     * 对应索引
     */
    private String key;
    /**
     * 扩展信息
     */
    private String extra;
    /**
     * 权限
     */
    private String privileges;
    /**
     * 备注
     */
    private String comment;
    /**
     * 排序规则
     */
    private String collation;

    /**
     * 是否为空
     */
    private String isNull;
    /**
     * 默认值
     */
    private String defaultValue;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNullFlag() {
        return nullFlag;
    }

    public void setNullFlag(String nullFlag) {
        this.nullFlag = nullFlag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
