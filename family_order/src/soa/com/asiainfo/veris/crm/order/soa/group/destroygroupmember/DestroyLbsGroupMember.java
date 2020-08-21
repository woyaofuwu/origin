
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyLbsGroupMember extends DestroyGroupMember
{
    public DestroyLbsGroupMember()
    {

    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        String now_cycle = SysDateMgr.getNowCycle();
        map.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        map.put("RSRV_STR2", relationTypeCode);
        map.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        map.put("RSRV_STR4", now_cycle);

        IData uuInfo = RelaUUInfoQry.getRelaByPK(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), relationTypeCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            return;
        }

        map.put("RSRV_STR5", uuInfo.getString("ROLE_CODE_B"));

        IData user = UcaInfoQry.qryUserInfoByUserId(reqData.getUca().getUserId());

        if (IDataUtil.isNotEmpty(user))
        {
            map.put("RSRV_STR6", user.getString("RSRV_STR6"));
            map.put("RSRV_STR7", user.getString("RSRV_STR3"));
            map.put("RSRV_STR8", user.getString("RSRV_STR4"));
        }
    }
}
