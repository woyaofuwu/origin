
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyDialInputMember extends DestroyGroupMember
{

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR4", SysDateMgr.getNowCyc());

        IDataset userRelationInfos = RelaUUInfoQry.checkMemRelaByUserIdb(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()), null);
        if (IDataUtil.isNotEmpty(userRelationInfos))
        {
            IData uuData = userRelationInfos.getData(0);
            data.put("RSRV_STR5", uuData.get("ROLE_CODE_B"));
        }

        data.put("RSRV_STR6", reqData.getUca().getUser().getRsrvStr6());
        data.put("RSRV_STR7", reqData.getUca().getUser().getRsrvStr3());
        data.put("RSRV_STR8", reqData.getUca().getUser().getRsrvStr4());

    }

}
