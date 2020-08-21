
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyTISmsGroupUser extends DestroyGroupUser
{
    /**
     * 构建函数
     */
    public DestroyTISmsGroupUser()
    {

    }

    /**
     * 生成其他台帐
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataTISmsPlatsvc();
    }

    private void infoRegDataTISmsPlatsvc() throws Exception
    {
        String user_id = reqData.getUca().getUserId();
        IDataset datas = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(user_id);
        if (IDataUtil.isEmpty(datas))
        {
            CSAppException.apperr(GrpException.CRM_GRP_636);
        }

        IData data = datas.getData(0);

        data.put("END_DATE", SysDateMgr.getSysTime());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        data.put("BIZ_STATE_CODE", "E");
        data.put("OPER_STATE", "1"); // 操作状态
        // data.put("RSRV_TAG2","");// PRODUCT_TYPE; 发往平台专用字段；
        data.put("RSRV_TAG3", "0");// 服务开通标识 0 正常走服务开通模式 1 ADC平台 2 行业网关

        IDataset discnts = reqData.cd.getDiscnt();
        String spCode = "";
        if (IDataUtil.isNotEmpty(discnts))
        {
            for (int i = 0, size = discnts.size(); i < size; i++)
            {
                IData discnt = discnts.getData(i);
                spCode = spCode + "|" + TRADE_MODIFY_TAG.DEL.getValue() + "~" + discnt.getString("ELEMENT_ID", "");
            }
        }
        data.put("RSRV_STR1", spCode); // SP_CODE
        addTradeGrpPlatsvc(data);

    }

    public void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR6", reqData.getUca().getUser().getRsrvStr6());
        data.put("RSRV_STR7", reqData.getUca().getUser().getRsrvStr7());
        data.put("RSRV_STR8", reqData.getUca().getUser().getRsrvStr8());
        data.put("RSRV_STR9", reqData.getUca().getUser().getRsrvStr9());
        data.put("RSRV_STR10", reqData.getUca().getUser().getRsrvStr10());
    }

    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR6", reqData.getUca().getUser().getRsrvStr6());
        map.put("RSRV_STR7", reqData.getUca().getUser().getRsrvStr7());
        map.put("RSRV_STR8", reqData.getUca().getUser().getRsrvStr8());
        map.put("RSRV_STR9", reqData.getUca().getUser().getRsrvStr9());
        map.put("RSRV_STR10", reqData.getUca().getUser().getRsrvStr10());
    }
}
