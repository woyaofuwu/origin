
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeGprsENetUserElement extends ChangeUserElement
{

    public ChangeGprsENetUserElement()
    {

    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        reqData.setIsChange(true);

    }

    public IData getTradeUserExtendData() throws Exception
    {
        IData userExtenData = super.getTradeUserExtendData();

        // 存产品产品信息到user表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);

        if (IDataUtil.isNotEmpty(productParams))
        {
            userExtenData.put("RSRV_STR6", productParams.getString("NOTIN_MGR_NAME"));
            userExtenData.put("RSRV_STR7", productParams.getString("NOTIN_MGR_PHONE"));
            userExtenData.put("RSRV_STR8", productParams.getString("NOTIN_DETADDRESS"));
            userExtenData.put("RSRV_STR9", productParams.getString("NOTIN_APN_NAME"));
            userExtenData.put("RSRV_STR10", reqData.getUca().getSerialNumber());
            userExtenData.put("REMARK", productParams.getString("NOTIN_APN_REMARK"));
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        return userExtenData;
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR6", paramData.getString("NOTIN_MGR_NAME", ""));
        data.put("RSRV_STR7", paramData.getString("NOTIN_MGR_PHONE", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETADDRESS", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_APN_NAME", ""));
        data.put("RSRV_STR10", reqData.getUca().getSerialNumber());
        data.put("REMARK", paramData.getString("NOTIN_APN_REMARK", ""));

    }

}
