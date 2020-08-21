
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestoryEconnectGroupMenber extends DestroyGroupMember
{
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData map = bizData.getTrade();
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId());
        map.put("RSRV_STR1", grpUserId); // 集团用户
        map.put("RSRV_STR2", relaTypeCode);// 关系类型编码
        map.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// A服务号码
        map.put("RSRV_STR4", SysDateMgr.getNowCyc());// 当前帐期
        IData uuInfo = RelaUUInfoQry.getRelaByPK(grpUserId, memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            return;
        }
        map.put("RSRV_STR5", uuInfo.getString("ROLE_CODE_B"));
        IData user = UcaInfoQry.qryUserInfoByUserId(memUserId);
        if (IDataUtil.isNotEmpty(user))
        {
            map.put("RSRV_STR6", user.getString("RSRV_STR6"));
            map.put("RSRV_STR7", user.getString("RSRV_STR3"));
            map.put("RSRV_STR8", user.getString("RSRV_STR4"));
        }

    }
}
