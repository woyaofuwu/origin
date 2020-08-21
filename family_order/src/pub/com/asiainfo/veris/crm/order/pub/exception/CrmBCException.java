package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CrmBCException implements IBusiException
{

    CRM_BC_1("%s"), //
    CRM_BC_5("未配置集团用户服务号码生成规则!"), //
    CRM_BC_7("传入数据不能为空！"), //
    CRM_BC_11("流程保存失败！"), //
    CRM_BC_8("查询条件不能为空！"), //
    CRM_BC_14("交互项标识不能为空"), //
    CRM_BC_15("%s"), //
    CRM_BC_16("无法获取用户的托收信息"), //
    CRM_BC_17("无法获取到参与人信息及证件信息"), //
    CRM_BC_18("该返销用户信息不存在"), //
    CRM_BC_19("无法获取到参与人信息及证件信息"), //
    CRM_BC_20("约束检查类必须是实现了IValidator接口的类"), //
    CRM_BC_21("该服务没有定义对应的@service注解，不能运行该服务"), //
    CRM_BC_22("没有找到%s所对应的treeList声明"), //
    CRM_BC_23("没有找到%s所有对应的tree声明"), //
    CRM_BC_24("part的name不能为空"), //
    CRM_BC_27("sheet的name不能为空"), //
    CRM_BC_25("根据name=%s找不到sheet的定义"), //
    CRM_BC_30("treeList的name不能为空"), //
    CRM_BC_31("根据name=%s找不到treeList的定义"), //
    CRM_BC_32("根据name=%s找不到treeList的定义"), //
    CRM_BC_33("tree的name不能为空"), //
    CRM_BC_34("根据name=%s找不到tree的定义"), //
    CRM_BC_35("根据name=%s找不到tree的定义"), //
    CRM_BC_36("sheet的列定义不能为空"), //
    CRM_BC_37("treeList对应的tree的列定义不能为空"), //
    CRM_BC_38("tree的列定义不能为空"), //
    CRM_BC_39("constraint的name不能为空"), //
    CRM_BC_40("根据name=%s找不到constraint的定义"), //
    CRM_BC_41("根据name=%s找不到constraint的定义"), //
    CRM_BC_42("没有找到SQL语句中变量%s对应的值，请检查程序"), //
    CRM_BC_43("数据源不能为空"), //
    CRM_BC_44("%s，该路径非目录"), //
    CRM_BC_45("没有找到对应的sheet定义"), //
    CRM_BC_46("List的数据类型必须是同一种，String或者Map"), //
    CRM_BC_47("不支持的返回数据类型"), //
    CRM_BC_48("未找到%s所对应的sheet定义"), //
    CRM_BC_49("未找到%s所对应的sheet定义"), //
    CRM_BC_50("%s所对应的sheet的列定义为空"), //
    CRM_BC_51("没有找到服务%s的配置定义"), //
    CRM_BC_52("没有找到服务%s的配置定义"), //
    CRM_BC_53("没有找到服务%s的配置定义"), //
    CRM_BC_54("%s"), //
    CRM_BC_55("未配置集团用户服务号码生成规则!"), //
    CRM_BC_56("读取CFG_BIZ_ENV参数[%s]为空"), //
    CRM_BC_57("根据操作员编码[%s]查询操作员信息为空!");

    private final String value;

    private CrmBCException(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

}
