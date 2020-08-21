
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyCentrexSuperTeleGroupMember extends DestroyGroupMember
{

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理VPN成员信息
        super.infoRegDataVpn();

        // 处理成员发送报文
        infoRegDataCentrexMeb();
    }

    /**
     * 处理成员发送报文信息
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexMeb() throws Exception
    {

        // 注销融合总机成员报文
        infoRegDataCentrexOther("24");

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IDataset relaInfoList = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null, null);

        if (IDataUtil.isNotEmpty(relaInfoList))
        {
            IData relaData = relaInfoList.getData(0);
            if (relaData.getString("ROLE_CODE_B", "").equals("3"))
            {
                // 注销话务员报文
                infoRegDataCentrexOther("22");
            }
        }
    }

    /**
     * 处理Other表信息
     * 
     * @param operCode
     *            处理标志
     * @throws Exception
     */
    public void infoRegDataCentrexOther(String operCode) throws Exception
    {
        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());// 成员用户ID
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_VALUE", "Centrex成员总机业务");
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "6301");// 服务ID
        centreData.put("RSRV_STR11", operCode);// 操作类型
        centreData.put("OPER_CODE", operCode);// 操作类型
        centreData.put("INST_ID", SeqMgr.getInstId());
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", getAcceptTime());

        super.addTradeOther(centreData);
    }

}
