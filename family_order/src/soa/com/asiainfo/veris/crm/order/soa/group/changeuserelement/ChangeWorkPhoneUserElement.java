
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeWorkPhoneUserElement extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(ChangeWorkPhoneUserElement.class);

    private String grpWPProduct = "";// 获取产品类型，入user表扩展字段5

    /**
     * 构造函数
     */
    public ChangeWorkPhoneUserElement()
    {

    }

    /**
     * @description 业务执行前处理
     * @author xuyt
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        IData productParam = getParamData();
        if (IDataUtil.isNotEmpty(productParam))
        {
            grpWPProduct = productParam.getString("WORKPHONE_CODE", ""); // 获取产品类型
        }
    }
    
    /**
     * @description 处理子表的数据-用户
     * @author xuyt
     * @date 2013-10-15
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行ChangeWorkPhoneUserElement类 getParamData() 得到产品页面传过来的参数：paramData=" + paramData + "<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
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

        tradeData.put("RSRV_STR6", paramData.getString("WORKPHONE_CODE", ""));

    }
    
    /**
     * 修改用户资料
     * 
     * @throws Exception
     */
    public IData getTradeUserExtendData() throws Exception
    {
        IData userData = super.getTradeUserExtendData();
        if (!grpWPProduct.equals(reqData.getUca().getUser().getRsrvStr5()))
        {
            userData.put("RSRV_STR6", grpWPProduct); // 获取产品类型
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        return userData;
    }
    

}
