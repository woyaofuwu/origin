
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserForegiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ModifyUserForegiftFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String invoiceNo = mainTrade.getString("INVOICE_NO");
        if (StringUtils.isBlank(invoiceNo))
        {
            modifyUserForegift(mainTrade);
        }
        else
        {
            modifyForegiftByInvoice(mainTrade);
        }

    }

    private void modifyForegiftByInvoice(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID", "-1");// 无主发票的时候取user_id=-1
        String custName = mainTrade.getString("CUST_NAME");// 客户名称
        String inDate = mainTrade.getString("ACCEPT_DATE");
        String execTime = mainTrade.getString("EXEC_TIME");
        String invoiceNo = mainTrade.getString("INVOICE_NO");
        String staffId = mainTrade.getString("TRADE_STAFF_ID");
        String departId = mainTrade.getString("TRADE_DEPART_ID");
        // String psptId = UcaInfoQry.qryCustomerInfoByCustId(mainTrade.getString("CUST_ID")).getString("PSPT_ID");

        IDataset feeTrade = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, "1");
        if (IDataUtil.isNotEmpty(feeTrade))
        {
            for (int i = 0; i < feeTrade.size(); i++)
            {
                IData feeData = feeTrade.getData(i);
                int fee = feeData.getInt("FEE");
                if (fee > 0)
                {
                    IData param = new DataMap();
                    param.put("USER_ID", userId);
                    param.put("SERVICE_MODE", "FG");
                    param.put("SERIAL_NUMBER", invoiceNo);
                    param.put("PROCESS_INFO", "押金发票号码信息");
                    param.put("RSRV_NUM1", feeData.getString("FEE_TYPE_CODE"));
                    param.put("RSRV_NUM2", fee);
                    param.put("RSRV_STR1", tradeId);
                    param.put("RSRV_STR2", custName);
                    param.put("RSRV_STR8", staffId);
                    param.put("RSRV_STR9", departId);
                    param.put("PROCESS_TAG", "0");
                    param.put("STAFF_ID", staffId);
                    param.put("DEPART_ID", departId);
                    param.put("START_DATE", inDate);
                    param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    param.put("REMARK", "");
                    param.put("PARTITION_ID", Long.valueOf(userId) % 10000);
                    param.put("INST_ID", SeqMgr.getInstId());

                    Dao.insert("TF_F_USER_OTHERSERV", param);
                }
                else if (fee == 0)
                {
                    // 如果押金是0元的时候不要处理
                }
                else
                {
                    if ("-1".equals(userId))
                    {
                        IDataset otherserv = UserOtherInfoQry.getInvoiceInfo(invoiceNo, "FG");
                        if (IDataUtil.isNotEmpty(otherserv))
                        {
                            userId = otherserv.getData(0).getString("USER_ID");
                            IData param = new DataMap();
                            param.put("MONEY", fee);
                            param.put("FOREGIFT_IN_DATE", inDate);
                            param.put("FOREGIFT_OUT_DATE", inDate);
                            param.put("UPDATE_TIME", execTime);
                            param.put("CUST_NAME", otherserv.getData(0).getString("RSRV_STR2"));// 取无主发票的用户名称
                            param.put("PSPT_ID", "");
                            param.put("USER_ID", otherserv.getData(0).getString("USER_ID"));// 取无主发票的用户标识
                            param.put("FOREGIFT_CODE", feeData.getString("FEE_TYPE_CODE"));
                            param.put("UPDATE_STAFF_ID", staffId);
                            param.put("UPDATE_DEPART_ID", departId);
                            param.put("REMARK", "无主发票押金清退");

                            Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "UPD_MONEY", param);
                        }
                    }
                    IData param = new DataMap();
                    param.put("PROCESS_TAG", "1");
                    param.put("USER_ID", userId);
                    param.put("STAFF_ID", staffId);
                    param.put("DEPART_ID", departId);
                    param.put("SERIAL_NUMBER", invoiceNo);
                    param.put("RSRV_STR3", tradeId); // 清退流水号
                    param.put("SERVICE_MODE", "FG");
                    param.put("REMARK", "根据userid、serialnumber清退押金");
                    param.put("RSRV_NUM1", feeData.getString("FEE_TYPE_CODE"));

                    Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPDATE_BY_USERID_SERIALNUMBER", param);
                }
            }
        }
    }

    private void modifyUserForegift(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String custName = mainTrade.getString("CUST_NAME");// 客户名称
        String inDate = mainTrade.getString("ACCEPT_DATE");
        String execTime = mainTrade.getString("EXEC_TIME");
        String remark = mainTrade.getString("REMARK", "");
        String psptId = UcaInfoQry.qryCustomerInfoByCustId(mainTrade.getString("CUST_ID")).getString("PSPT_ID");
        String outDate = "";

        IDataset feeTrade = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, "1");
        if (IDataUtil.isNotEmpty(feeTrade))
        {
            for (int i = 0; i < feeTrade.size(); i++)
            {
                IData feeData = feeTrade.getData(i);
                String foregiftCode = feeData.getString("FEE_TYPE_CODE");
                int feeValue = feeData.getInt("FEE", 0);
                String staffId = feeData.getString("UPDATE_STAFF_ID");
                String departId = feeData.getString("UPDATE_DEPART_ID");
                if (feeValue > 0)
                {// foregiftValue小于0时为清退押金
                    outDate = "";
                }
                else
                {
                    outDate = execTime;
                }

                IDataset foregift = UserForegiftInfoQry.getUserForegift(userId, foregiftCode);
                if (IDataUtil.isNotEmpty(foregift))
                {
                    IData param = new DataMap();
                    param.put("MONEY", feeValue);
                    param.put("FOREGIFT_IN_DATE", inDate);
                    param.put("FOREGIFT_OUT_DATE", outDate);
                    param.put("UPDATE_TIME", execTime);
                    param.put("CUST_NAME", custName);
                    param.put("PSPT_ID", psptId);
                    param.put("USER_ID", userId);
                    param.put("FOREGIFT_CODE", foregiftCode);
                    param.put("UPDATE_STAFF_ID", staffId);
                    param.put("UPDATE_DEPART_ID", departId);
                    param.put("REMARK", remark);

                    Dao.executeUpdateByCodeCode("TF_F_USER_FOREGIFT", "UPD_MONEY", param);
                }
                else
                {
                    IData param = new DataMap();
                    param.put("USER_ID", userId);
                    param.put("FOREGIFT_CODE", foregiftCode);
                    param.put("MONEY", feeValue);
                    param.put("CUST_NAME", custName);
                    param.put("PSPT_ID", psptId);
                    param.put("FOREGIFT_IN_DATE", inDate);
                    param.put("FOREGIFT_OUT_DATE", outDate);
                    param.put("UPDATE_TIME", execTime);
                    param.put("UPDATE_STAFF_ID", staffId);
                    param.put("UPDATE_DEPART_ID", departId);
                    param.put("REMARK", remark);
                    param.put("RSRV_NUM1", -1);
                    param.put("RSRV_NUM2", -1);
                    param.put("RSRV_NUM3", -1);
                    param.put("RSRV_NUM4", -1);
                    param.put("RSRV_NUM5", -1);
                    param.put("PARTITION_ID", Long.valueOf(userId) % 10000);

                    Dao.insert("TF_F_USER_FOREGIFT", param);
                }
            }
        }
    }

}
