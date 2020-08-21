
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: InsertPPSPayLogAction.java
 * @Description: 记录铁通智能网缴费
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-26 上午10:53:56 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-26 yxd v1.0.0 修改原因
 */
public class InsertPPSPayLogAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset feesubList = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, BofConst.FEE_MODE_OPERFEE);
        if (DataSetUtils.isNotBlank(feesubList))
        {
            for (Object obj : feesubList)
            {
                IData feeData = (IData) obj;
                if (StringUtils.equals("9", feeData.getString("FEE_MODE")) && !StringUtils.equals("0", feeData.getString("FEE")))
                {
                    IData param = new DataMap();
                    param.put("CHARGE_ID", tradeId);
                    param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
                    param.put("CUST_ID", feeData.getString("CUST_ID"));
                    param.put("ACCT_ID", feeData.getString("ACCT_ID"));
                    param.put("USER_ID", feeData.getString("USER_ID"));
                    param.put("SERIAL_NUMBER", feeData.getString("SERIAL_NUMBER"));
                    param.put("CHANNEL_ID", "15000");
                    param.put("PAYMENT_ID", "1");
                    param.put("PAY_FEE_MODE_CODE", "0");
                    param.put("PAYMENT_OP", "0");
                    param.put("RECV_FEE", feeData.getString("FEE"));
                    param.put("RECV_TIME", feeData.getString("EFFICET_DATE"));
                    param.put("RECV_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    param.put("RECV_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    param.put("RECV_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    param.put("RECV_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("ACT_TAG", "0");
                    param.put("CANCEL_TAG", feeData.getString("CANCEL_TAG"));
                    AcctCall.insertPPSPayLog(param);
                }
            }
        }
    }
}
