
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg;

public final class SaleActiveCommparaBreConst
{
    public static final String PARAM_CODE_VALUE = "SALEACTIVE";
    
    public static final String PARAM_ATTR_RULE_9999 = "9999";//测试用规则编码
    public static final String PARAM_ATTR_RULE_670 = "670";//用户办理了A或B或C活动，才可办理D活动
    public static final String PARAM_ATTR_RULE_671 = "671";//用户存在A活动下A1营销包，则不允许办理B活动下B1营销包
    public static final String PARAM_ATTR_RULE_672 = "672";//判断用户办理的活动，购买过某个机型的终端（通过机型编码判断，可配置多个机型编码，只要其中一个有效即可），才办理某个活动下某个营销包
}
