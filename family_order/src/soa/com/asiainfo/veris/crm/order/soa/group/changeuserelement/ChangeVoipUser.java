
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeVoipUser extends ChangeUserElement
{
    public ChangeVoipUser()
    {

    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    /**
     * 判断是否修改了用户表中的合同信息
     * 
     * @throws Exception
     */
    public IData getTradeUserExtendData() throws Exception
    {
        IData userExtenData = super.getTradeUserExtendData();

        // 存产品产品信息到user表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);

        if (IDataUtil.isNotEmpty(productParams))
        {
            userExtenData.put("RSRV_STR7", productParams.getString("NOTIN_DETMANAGER_INFO"));
            userExtenData.put("RSRV_STR8", productParams.getString("NOTIN_DETMANAGER_PHONE"));
            userExtenData.put("RSRV_STR9", productParams.getString("NOTIN_DETADDRESS"));
            userExtenData.put("RSRV_STR10", productParams.getString("NOTIN_PROJECT_NAME"));
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        return userExtenData;
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));
        data.put("RSRV_STR10", paramData.getString("NOTIN_PROJECT_NAME", ""));

    }

    protected void setTradeBase() throws Exception
    {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("OLCOM_TAG", "0");

    }

}
