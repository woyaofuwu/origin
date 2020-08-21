/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;

/**
 * @CREATED by gongp@2014-6-30 修改历史 Revision 2014-6-30 下午09:02:45
 */
public class GeneBankSubsignAction implements ITradeAction
{

    /*
     * (non-Javadoc) 标准完工里已经做了，未使用
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        // 总对总缴费关联副号码同步处理
        List<BankSubSignTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_BANK_SUBSIGN);
        String tradeId = btd.getTradeId();

        if (!list.isEmpty())
        {
            for (int i = 0, size = list.size(); i < size; i++)
            {
                BankSubSignTradeData subSignTd = list.get(i);

                String sqlBillsynId = SeqMgr.getBillSynId();

                IData tiBSynchInfo = new DataMap();

                tiBSynchInfo.put("SYNC_SEQUENCE", sqlBillsynId);
                tiBSynchInfo.put("MODIFY_TAG", subSignTd.getModifyTag());
                tiBSynchInfo.put("TRADE_ID", tradeId);
                tiBSynchInfo.put("RSRV_STR12", subSignTd.getSubUserType());
                tiBSynchInfo.put("RSRV_STR13", subSignTd.getSubUserValue());
                tiBSynchInfo.put("START_DATE", subSignTd.getStartDate());

                Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INC_TI_BANK_SUBSIGN", tiBSynchInfo);

                tiBSynchInfo.clear();

                tiBSynchInfo.put("SYNC_SEQUENCE", sqlBillsynId);
                tiBSynchInfo.put("TRADE_ID", tradeId);

                Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INS_SYNCHINFO", tiBSynchInfo);

            }
        }
    }

}
