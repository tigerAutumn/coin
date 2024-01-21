package com.hotcoin.notice.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseModel extends Object implements Serializable {
  /** 最大行数. */
  private static final int MAX_ROWS = 9999999;

  private static final long serialVersionUID = 1L;

  /**
   * 动态字段. 在Mybatis文件中可用“dynamicFields.xxx”方式读取动态字段值
   */
  protected Map<String, Object> dynamicFields = new HashMap<>();

  /**
   * 排序字段(例sp.spCode).
   */
  private String orderBy;

  /**
   * 正序|反序(例ASC).
   */
  private String orderDir;

  /**
   * 结束行数（如果不设置结束行，缺省查所有满足条件记录）.
   */
  private int pageNo = 1;


  private int pageSize = MAX_ROWS;

  /** 起始行数（oracle物理行号从1开始）. */
  private int start = 0;

  public Map<String, Object> getDynamicFields() {
    if (dynamicFields != null && dynamicFields.size() > 0) {
      for (Map.Entry<String, Object> entry : dynamicFields.entrySet()) {
        String key = entry.getKey();
        if (dynamicFields.get(key) != null && dynamicFields.get(key).getClass().isArray()) {
          Object[] objArr = (Object[]) dynamicFields.get(key);
          if (objArr.length == 1) {
            dynamicFields.put(key, ((Object[]) dynamicFields.get(key))[0]);
          }
        }
      }
    }
    return dynamicFields;
  }


  /**
   * 返回动态字段值.
   *
   * @param fieldName 字段名称
   * @return 对象
   */
  public Object getField(String fieldName) {
    if (dynamicFields == null) {
      return null;
    }
    return getDynamicFields().get(fieldName);
  }

  public String getOrderBy() {
    return orderBy;
  }

  public String getOrderDir() {
    return orderDir;
  }

  public int getPageNo() {
    return pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  /**
   * @return the start
   */
  public int getStart() {
    return start;
  }

  public void setDynamicFields(Map<String, Object> dynamicFields) {
    this.dynamicFields = dynamicFields;
  }

  /**
   * 设置动态字段值.
   *
   * @param fieldName 字段名称
   * @param value 字段值
   */
  public void setField(String fieldName, Object value) {
    // if (dynamicFields == null) {
    // dynamicFields = new HashMap();
    // }
    dynamicFields.put(fieldName, value);
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public void setOrderDir(String orderDir) {
    this.orderDir = orderDir;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * @param start the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }
}
