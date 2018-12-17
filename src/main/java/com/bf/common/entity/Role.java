package com.bf.common.entity;

/**
 * 角色实体类
 * Created by bf on 2017/8/9.
 */
public class Role extends Base {

    /** 序号*/
    private Integer num;

    /** 父角色id*/
    private Integer pid;

    /** 角色名称*/
    private String  name;

    /** 部门名称*/
    private Integer deptid;

    /** 提示*/
    private String  tips;

    /**  保留字段(暂时没用）*/
    private Integer version;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDeptid() {
        return deptid;
    }

    public void setDeptid(Integer deptid) {
        this.deptid = deptid;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Role{" +
                "num=" + num +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", deptid=" + deptid +
                ", tips='" + tips + '\'' +
                ", version=" + version +
                '}';
    }
}
