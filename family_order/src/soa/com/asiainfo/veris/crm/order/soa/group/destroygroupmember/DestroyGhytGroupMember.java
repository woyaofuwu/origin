
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyGhytGroupMember extends DestroyGroupMember
{
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    protected void prepareSucSmsData(IData smsData) throws Exception
    {
        super.prepareSucSmsData(smsData);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData map = bizData.getTrade();
        map.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团用户
        map.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId()));// 关系类型编码
        map.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// A服务号码
        map.put("RSRV_STR4", SysDateMgr.getNowCyc());// 当前帐期
        map.put("RSRV_STR8", reqData.getUca().getCustomer().getCustName());// 成员客户名称
        map.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团编号
    }

}
