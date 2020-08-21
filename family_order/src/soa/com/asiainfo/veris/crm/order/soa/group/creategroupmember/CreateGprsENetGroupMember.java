
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateGprsENetGroupMember extends CreateGroupMember
{

    public CreateGprsENetGroupMember() throws Exception
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

        IData param = reqData.cd.getProductParamMap(reqData.getBaseMebProductId());

        IData data = new DataMap();
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("RSRV_VALUE_CODE", "VGPR");
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());

        data.put("RSRV_VALUE", param.getString("NOTIN_USE_APN"));
        data.put("RSRV_STR1", param.getString("NOTIN_IP_ADDRESS"));
        data.put("RSRV_STR4", reqData.getGrpUca().getUserId());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataset.add(data);

        addTradeOther(dataset);
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
