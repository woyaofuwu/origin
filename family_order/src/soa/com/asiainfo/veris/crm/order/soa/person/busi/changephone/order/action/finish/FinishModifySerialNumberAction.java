
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * 改号完工
 * 
 * @author wangf
 */
public class FinishModifySerialNumberAction implements ITradeFinishAction
{

    public void changeSimCardFinish(IData mainTrade) throws Exception
    {
        IDataset resSet = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "1", BofConst.MODIFY_TAG_ADD);
        IDataset oldResSet = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "1", BofConst.MODIFY_TAG_DEL);
        IDataset phoneSet = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "0", BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(resSet) && IDataUtil.isNotEmpty(phoneSet))
        {
            // 号码占用
            ResCall.modifyPhone(resSet.getData(0).getString("RES_CODE"), resSet.getData(0).getString("IMSI"), mainTrade.getString("SERIAL_NUMBER"), mainTrade.getString("RSRV_STR2"), mainTrade.getString("PRODUCT_ID"), mainTrade.getString("TRADE_ID"),
                    mainTrade.getString("ACCEPT_DATE"), "0", mainTrade.getString("TRADE_TYPE_CODE"));
            // SIM卡占用
            ResCall.modifySimCard("0", mainTrade.getString("RSRV_STR2"), oldResSet.getData(0).getString("RES_CODE"), resSet.getData(0).getString("RES_CODE"), "0", mainTrade.getString("TRADE_ID"), mainTrade.getString("TRADE_TYPE_CODE"), "1",
                    mainTrade.getString("USER_ID"), mainTrade.getString("PRODUCT_ID"));
        }
    }

    public void dealResInfo(IData mainTrade) throws Exception
    {
        IDataset resSet = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "0", BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(resSet))
        {
            String newSn = resSet.getData(0).getString("RES_CODE");
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", newSn);
            param.put("USER_ID", resSet.getData(0).getString("USER_ID"));
            // Dao.executeUpdateByCodeCode("TF_F_USER", "UPD_SN_BY_ID", param);
            // Dao.executeUpdateByCodeCode("TF_F_USER", "UPD_CITY_BY_SN", param);//修改业务区，必须同业务区，取消
            Dao.executeUpdateByCodeCode("TF_F_USER_SHARE_RELA", "UPD_SN_BY_ID", param);
            // Dao.executeUpdateByCodeCode("TF_F_RELATION_UU", "UPD_SN_BY_ID", param);
            // Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SN_BY_ID", param);//用户营销活动
            // Dao.executeUpdateByCodeCode("TF_F_USER_RENT", "UPD_SN_BY_ID", param);//用户租机业务
            Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_SN_BY_ID", param);
            Dao.executeUpdateByCodeCode("TF_F_VPMN_GROUP_MEMBER", "UPD_SN_BY_ID", param);
        }
    }

    public void executeAction(IData mainTrade) throws Exception
    {
        changeSimCardFinish(mainTrade); // 资源占用
        dealResInfo(mainTrade);
    }

}
