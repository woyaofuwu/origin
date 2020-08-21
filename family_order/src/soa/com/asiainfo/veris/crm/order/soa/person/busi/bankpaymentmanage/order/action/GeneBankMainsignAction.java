/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-30 修改历史 Revision 2014-6-30 下午07:31:12
 */
public class GeneBankMainsignAction implements ITradeFinishAction
{
    /*
     * (non-Javadoc) 标准完工里已经做了，未使用
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset dataset = UserBankMainSignInfoQry.qryTradeMainsignInfoByPK(tradeId);

        if (IDataUtil.isNotEmpty(dataset))
        {

            for (int i = 0, size = dataset.size(); i < size; i++)
            {

                IData temp = dataset.getData(i);

                String sqlBillsynId = SeqMgr.getBillSynId();

                IData tiBSynchInfo = new DataMap();

                tiBSynchInfo.put("SYNC_SEQUENCE", sqlBillsynId);
                tiBSynchInfo.put("MODIFY_TAG", temp.getString("MODIFY_TAG"));
                tiBSynchInfo.put("TRADE_ID", tradeId);
                tiBSynchInfo.put("RSRV_STR12", temp.getString("USER_TYPE"));
                tiBSynchInfo.put("RSRV_STR13", temp.getString("USER_VALUE"));
                tiBSynchInfo.put("START_DATE", temp.getString("START_DATE"));

                Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INC_TI_BANK_MAINSIGN", tiBSynchInfo);

                // 插入总表
                tiBSynchInfo.clear();

                tiBSynchInfo.put("SYNC_SEQUENCE", sqlBillsynId);
                tiBSynchInfo.put("TRADE_ID", tradeId);

                Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INS_SYNCHINFO", tiBSynchInfo);
            }
        }
    }
}
