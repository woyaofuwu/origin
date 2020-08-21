
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyCentrexVpnGroupUser extends DestroyGroupUser
{
    private IDataset relationUUs = null; // 集团和号码组的关系

    public DestroyCentrexVpnGroupUser()
    {

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 获取集团下网外号码
        getRelaData();

        infoRegDataVpn();
        infoRegDataCentreOther();
        if (IDataUtil.isNotEmpty(relationUUs))
        {
            infoRegDataRela();
        }
    }

    /**
     * 获取集团对应的网外号码用户
     * 
     * @throws Exception
     */
    public void getRelaData() throws Exception
    {
        String user_id_a = reqData.getUca().getUserId();
        relationUUs = RelaUUInfoQry.getRelationUUbYUserIDa2(user_id_a, null);
    }

    /**
     * 融合V网 登记平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String custId = reqData.getUca().getCustId();
        String instId = SeqMgr.getInstId();

        IDataset dataset = new DatasetList();
        IData centrexData = new DataMap();

        IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfo = DataHelper.filter(uservpninfos, "USER_ID=" + userId);
        // 如果只有一条记录并且为此集团USER_ID则删除集团指令,其它则发退定哪个业务
        if (uservpninfos.size() == 1 && uservpninfo.size() == 1)
        {
            centrexData.put("USER_ID", userId);
            centrexData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centrexData.put("RSRV_VALUE", "删除集团");
            centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centrexData.put("RSRV_STR9", "817"); // 服务id
            centrexData.put("OPER_CODE", "02"); // 操作类型 02 注销
            centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centrexData.put("START_DATE", getAcceptTime());
            centrexData.put("END_DATE", getAcceptTime()); // 立即截止
            centrexData.put("INST_ID", instId);
            dataset.add(centrexData);
        }
        else
        {
            centrexData.put("USER_ID", userId);
            centrexData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
            centrexData.put("RSRV_VALUE", "Centrex集团融合V网业务退订");
            centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
            centrexData.put("RSRV_STR9", "8001"); // 服务id
            centrexData.put("OPER_CODE", "07"); // 操作类型 07 退订
            centrexData.put("RSRV_STR21", "VPMNBG"); // 标识退订的集团业务 VPMNBG 融合V网
            centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centrexData.put("START_DATE", getAcceptTime());
            centrexData.put("END_DATE", getAcceptTime()); // 立即截止
            centrexData.put("INST_ID", instId);
            dataset.add(centrexData);
        }
        addTradeOther(dataset);
    }

    /**
     * 关系注销
     * 
     * @throws Exception
     */
    public void infoRegDataRela() throws Exception
    {
        for (int i = 0; i < relationUUs.size(); i++)
        {
            IData relationUU = relationUUs.getData(i);
            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            relationUU.put("END_DATE", getAcceptTime());
        }
        this.addTradeRelation(relationUUs);
    }

}
