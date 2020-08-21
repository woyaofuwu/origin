
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeJTCLBUserElement extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(ChangeJTCLBUserElement.class);

    private String grpWPProduct = "";// 获取产品类型，入user表扩展字段5

    /**
     * 构造函数
     */
    public ChangeJTCLBUserElement()
    {

    }

    /**
     * 业务执行前处理
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        IData productParam = getParamData();
    }
    
    /**
     * 处理子表的数据-用户
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-5-29
     */
    public IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行ChangeZunXKHUserElement类 getParamData() 得到产品页面传过来的参数：paramData=" + paramData + "<<<<<<<<<<<<<<<<<<<");
        return paramData;
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }


    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
    }
    
    /**
     * 修改用户资料
     */
    public IData getTradeUserExtendData() throws Exception
    {
        IData userData = super.getTradeUserExtendData();
        return userData;
    }
    

}
