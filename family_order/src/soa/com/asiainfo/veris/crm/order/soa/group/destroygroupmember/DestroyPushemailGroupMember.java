
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyPushemailGroupMember extends DestroyGroupMember
{

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        IDataset dataset = new DatasetList();

        dataset = reqData.cd.getSpSvc();

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("RSRV_DATE1", getAcceptTime()); // 当前时间；平台开通报文用;
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR4", SysDateMgr.getNowCycle());
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR8", reqData.getUca().getCustPerson().getCustName());

    }
}
