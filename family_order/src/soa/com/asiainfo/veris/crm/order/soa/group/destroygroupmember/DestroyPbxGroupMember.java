
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyPbxGroupMember extends DestroyGroupMember
{

    public DestroyPbxGroupMember()
    {

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

    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR4", "201404");

        IData imparam = new DataMap();
        imparam.put("USER_ID_A", reqData.getGrpUca().getUserId());
        imparam.put("USER_ID_B", reqData.getUca().getUserId());
        imparam.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));

        IDataset userRelationInfos = RelaUUInfoQry.checkMemRelaByUserIdb(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()), null);
        if (null != userRelationInfos && userRelationInfos.size() > 0)
        {
            IData uuData = userRelationInfos.getData(0);
            data.put("RSRV_STR5", uuData.get("ROLE_CODE_B"));
        }

        data.put("RSRV_STR6", reqData.getUca().getUser().getRsrvStr6());
        data.put("RSRV_STR7", reqData.getUca().getUser().getRsrvStr3());
        data.put("RSRV_STR8", reqData.getUca().getUser().getRsrvStr4());

    }

}
