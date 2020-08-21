
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class UnicomTransferFinishAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        IData dataMap = new DataMap();
        dataMap.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        // 删除
        if (BofConst.MODIFY_TAG_DEL.equals(mainTrade.getString("RSRV_STR6", "")))
        {
            dataMap.put("PHONE_CODE_A", mainTrade.getString("RSRV_STR1", ""));
            dataMap.put("PHONE_CODE_B", mainTrade.getString("RSRV_STR2", ""));
            dataMap.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
            Dao.executeUpdateByCodeCode("TI_B_TRANS_PHONE", "UPD_TRANS_PHONE", dataMap);
        }
        else
        {
            dataMap.put("PHONE_CODE_A", mainTrade.getString("RSRV_STR1", ""));
            dataMap.put("PHONE_CODE_B", mainTrade.getString("RSRV_STR2", ""));
            dataMap.put("START_DATE", mainTrade.getString("RSRV_STR3", ""));
            dataMap.put("END_DATE", mainTrade.getString("RSRV_STR4", ""));

            // 新增
            if (BofConst.MODIFY_TAG_ADD.equals(mainTrade.getString("RSRV_STR6", "")))
            {
                dataMap.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                dataMap.put("UPDATE_TIME", mainTrade.getString("RSRV_STR5", ""));
                Dao.executeUpdateByCodeCode("TI_B_TRANS_PHONE", "INS_TRANS_PHONE", dataMap);
                // 修改
            }
            else if (BofConst.MODIFY_TAG_UPD.equals(mainTrade.getString("RSRV_STR6", "")))
            {
                dataMap.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
                Dao.executeUpdateByCodeCode("TI_B_TRANS_PHONE", "UPD_MODIFY", dataMap);
            }

        }

    }

}
