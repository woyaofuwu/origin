
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyYhtGroupUser extends DestroyGroupUser
{
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * @description 生成其它台帐数据（生成台帐后）
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVpn();
        infoRegOtherData();
    }

    public void infoRegOtherData() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String custId = reqData.getUca().getCustId();

        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();

        IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfo = DataHelper.filter(uservpninfos, "USER_ID=" + userId);
        // 如果只有一条记录并且为此集团USER_ID则删除集团指令,其它则发退定哪个业务
        if (uservpninfos.size() == 1 && uservpninfo.size() == 1)
        {
            centreData.put("USER_ID", userId);
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "删除集团");
            centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centreData.put("RSRV_STR9", "817"); // 服务id
            centreData.put("OPER_CODE", "02"); // 操作类型 02 注销
            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", getAcceptTime()); // 立即截止
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
        }
        addTradeOther(dataset);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
}
