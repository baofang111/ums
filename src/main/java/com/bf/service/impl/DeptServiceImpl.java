package com.bf.service.impl;

import com.bf.common.entity.Dept;
import com.bf.dao.DeptMapper;
import com.bf.service.DeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bf on 2017/8/9.
 */
@Service
@Transactional
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;


    @Override
    public String getDeptName(Integer deptId) {
        if( 0 == deptId){
            return "";
        }
        Dept dept = deptMapper.selectByPrimaryKey(deptId);
        if(dept != null && StringUtils.isNotBlank(dept.getFullname())){
            return dept.getFullname();
        }
        return "";
    }
}
