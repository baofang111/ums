package com.bf.service;

/**
 * 部门service
 * Created by bf on 2017/8/9.
 */
public interface DeptService {

    /**
     * 根据部门ID 获取部门名称
     * @param deptId
     * @return
     */
    String getDeptName(Integer deptId);
}
