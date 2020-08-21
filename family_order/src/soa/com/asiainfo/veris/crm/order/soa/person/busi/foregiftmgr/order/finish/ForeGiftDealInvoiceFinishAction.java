/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

/**
 * @CREATED by gongp@2014-5-22 修改历史 Revision 2014-5-22 下午08:03:06
 */
public class ForeGiftDealInvoiceFinishAction implements ITradeFinishAction
{

    // @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String strInvoiceNo = mainTrade.getString("INVOICE_NO");
        String strCustName = mainTrade.getString("CUST_NAME");
        String strCancelTag = mainTrade.getString("CANCEL_TAG");// 用于处理返销 sunxin
        String strTradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
        String strTradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
        String strInDate = mainTrade.getString("ACCEPT_DATE");

        IDataset dataset = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, BofConst.FEE_MODE_FOREGIFT);

        for (int i = 0, size = dataset.size(); i < size; i++)
        {

            IData feeData = dataset.getData(i);

            int ifee = feeData.getInt("FEE");

            if (ifee > 0 && "0".equals(strCancelTag))
            {

                IData param = new DataMap();

                param.put("USER_ID", userId);

                param.put("SERVICE_MODE", "FG");
                param.put("SERIAL_NUMBER", strInvoiceNo);
                param.put("PROCESS_INFO", "押金发票号码信息");
                param.put("RSRV_NUM1", feeData.getString("FEE_TYPE_CODE"));
                param.put("RSRV_NUM2", ifee);
                param.put("RSRV_NUM3", "");
                param.put("RSRV_STR1", tradeId);
                param.put("RSRV_STR2", strCustName);

                param.put("RSRV_STR3", "");
                param.put("RSRV_STR4", "");
                param.put("RSRV_STR5", "");
                param.put("RSRV_STR6", "");
                param.put("RSRV_STR7", "");

                param.put("RSRV_STR8", strTradeStaffId);
                param.put("RSRV_STR9", strTradeDepartId);
                param.put("RSRV_STR10", "");
                param.put("RSRV_DATE1", "");
                param.put("RSRV_DATE2", "");
                param.put("RSRV_DATE3", "");
                param.put("PROCESS_TAG", "0");

                param.put("STAFF_ID", strTradeStaffId);
                param.put("DEPART_ID", strTradeDepartId);
                param.put("START_DATE", strInDate);
                param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                param.put("REMARK", "");
                param.put("INST_ID", SeqMgr.getInstId());

                Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "INS_ALL", param);
            }
            else if (ifee < 0 || ("2".equals(strCancelTag)) || ("3".equals(strCancelTag)))// 处理返销 sunxin
            {

                IData param = new DataMap();

                param.put("USER_ID", feeData.getString("USER_ID"));

                param.put("SERVICE_MODE", "FG");
                param.put("SERIAL_NUMBER", strInvoiceNo);
                param.put("REMARK", "根据userid、serialnumber清退押金");
                param.put("RSRV_NUM1", feeData.getString("FEE_TYPE_CODE"));

                param.put("RSRV_STR3", tradeId);
                param.put("PROCESS_TAG", "1");

                param.put("STAFF_ID", strTradeStaffId);
                param.put("DEPART_ID", strTradeDepartId);

                Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPDATE_BY_USERID_SERIALNUMBER", param);
            }
            else
            {

            }
        }
    }
}
