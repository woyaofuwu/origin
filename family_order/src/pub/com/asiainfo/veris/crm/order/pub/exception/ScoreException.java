
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ScoreException implements IBusiException
{
	CRM_SCORE_0("[%s]"),
    CRM_SCORE_1("服务号码不能为空"), 
    CRM_SCORE_2("获取用户赠送积分总值无数据"), 
    CRM_SCORE_3("获取用户被赠送积分总值无数据空"), 
    CRM_SCORE_4("用户积分账户资料不存在"), 
    CRM_SCORE_5("用户积分账户关系资料不存在"), 
    
    
    CRM_SCORE_6("通知和生活平台下发电子券失败"), 
    CRM_SCORE_7("活动有效期已过"),
    CRM_SCORE_8("有效期内用户已办理过该活动"),
    CRM_SCORE_9("非目标VIP客户"),
    CRM_SCORE_10("非[%s]目标客户群"),
    //CRM_SCORE_11("未配置活动有效期"),
    
    CRM_SCORE_11("用户不存在"),//1
    CRM_SCORE_12("CRM无该礼品兑换编码"),//2
    CRM_SCORE_13("员工及代理商无法参加"),//3
    CRM_SCORE_14("非目标星级客户"),//4
    CRM_SCORE_15("非目标地区客户"),//5
    CRM_SCORE_16("未配置活动有效期"),//6
    CRM_SCORE_17("活动有效期已过"),//7
    CRM_SCORE_18("有效期内用户已办理过该活动"),//8
    CRM_SCORE_19("获取用户积分无数据"),//9
    CRM_SCORE_20("用户积分为[%s],所需积分最少为[%s],不足本次兑换！"),//10
	CRM_SCORE_21("获取积分兑换电子券金额配置失败"),
    CRM_SCORE_22("电子券金额不能为空"),
    CRM_SCORE_23("活动的参加人数已经满额，无法继续参加！"),
    CRM_SCORE_25("用户入网未满6个月，无法继续办理该业务！"),
    CRM_SCORE_26("转出用户入网未满6个月，无法继续办理该业务！"),
    CRM_SCORE_27("转入用户入网未满6个月，无法继续办理该业务！");

    private final String value;

    private ScoreException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
