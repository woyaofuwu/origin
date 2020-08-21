
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeGprsEWayMemElement extends ChangeMemElement
{

    public ChangeGprsEWayMemElement() throws Exception
    {
        super();
    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        regOtherInfoTrade();

    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    public void regOtherInfoTrade() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData userData = null;
        IData param = reqData.cd.getProductParamMap(reqData.getBaseMebProductId());
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", reqData.getUca().getUserId());
        userInParam.put("RSRV_VALUE_CODE", "VGPR");
        userInParam.put("USER_ID_A", reqData.getGrpUca().getUserId());

        IDataset userOtherInfo = TradeOtherInfoQrySVC.queryUserOtherInfoByUserId(userInParam);

        if (null != userOtherInfo && userOtherInfo.size() > 0)
        {
            userData = (IData) userOtherInfo.get(0);
            userData.put("RSRV_VALUE", param.getString("NOTIN_USE_APN"));
            userData.put("RSRV_STR1", param.getString("NOTIN_IP_ADDRESS"));
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        dataset.add(userData);
        addTradeOther(userData);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData param = reqData.cd.getProductParamMap(reqData.getBaseMebProductId());

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR4", reqData.getBaseMebProductId());
        data.put("RSRV_STR6", param.getString("NOTIN_USE_APN"));
        data.put("RSRV_STR7", param.getString("NOTIN_IP_ADDRESS"));

    }

}
