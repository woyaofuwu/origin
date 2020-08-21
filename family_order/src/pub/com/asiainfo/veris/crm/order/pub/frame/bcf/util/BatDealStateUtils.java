
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

public final class BatDealStateUtils
{
    public final static String DEAL_STATE_0 = "0"; // 批次未启动

    public final static String DEAL_STATE_1 = "1"; // 等待预处理

    public final static String DEAL_STATE_2 = "2"; // 正在预处理

    public final static String DEAL_STATE_3 = "3";// 预处理成功

    public final static String DEAL_STATE_4 = "4"; // 正在调用接口处理

    public final static String DEAL_STATE_5 = "5"; // 接口调用成功

    public final static String DEAL_STATE_6 = "6"; // 接口调用失败

    public final static String DEAL_STATE_7 = "7"; // 等待订单完工

    public final static String DEAL_STATE_8 = "8"; // 完工同步检查失败

    public final static String DEAL_STATE_9 = "9"; // 订单处理完成

    public final static String DEAL_STATE_A = "A"; // 等待依赖批次完成

    public final static String DEAL_STATE_B = "B"; // 服务开通回单成功

    public final static String DEAL_STATE_C = "C"; // 正在处理依赖

    public final static String DEAL_STATE_D = "D"; // 依赖处理失败

    public final static String DEAL_STATE_R = "R"; // 已存在相同业务数据，不做处理
    
    public final static String DEAL_STATE_S = "S"; // 发送服务开通成功
	
    public final static String DEAL_STATE_E = "E"; // 预处理失败
    
    public final static String DEAL_STATE_F = "F"; // ESOP的批量明细校验失败
    
    public final static String DEAL_STATE_M = "M"; // 服务开通错单

}
