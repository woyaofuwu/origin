
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

/**
 * 手机平台处理结果
 */
public class PlatInfoFinishAction implements ITradeFinishAction
{
    protected static Logger log = Logger.getLogger(PlatInfoFinishAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");

        IDataset platInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "54");
        if (IDataUtil.isNotEmpty(platInfos))
        {
            String bizStateCode = platInfos.getData(0).getString("BIZ_STATE_CODE");
            if ("A".equals(bizStateCode) || "N".equals(bizStateCode))
            {
                IData dealInfo = IBossCall.changCard2NotifyIBOSS(mainTrade.getString("SERIAL_NUMBER"), mainTrade.getString("RSRV_STR6"), mainTrade.getString("RSRV_STR7"), mainTrade.getString("IN_MODE_CODE"), mainTrade.getString("TRADE_TYPE_CODE"),
                        mainTrade.getString("TRADE_CITY_CODE"), mainTrade.getString("TRADE_DEPART_ID"), mainTrade.getString("UPDATE_STAFF_ID"), CSBizBean.getVisit().getProvinceCode(), "01", mainTrade.getString("SERIAL_NUMBER"));
                if (IDataUtil.isNotEmpty(dealInfo))
                {
                    String dealRes = dealInfo.getString("DEAL_RESULT");
                    if ("2001".equals(dealRes) || "2024".equals(dealRes) || "4005".equals(dealRes))
                    {
                        UserPlatSvcInfoQry.updDelUser(userId, "54");
                    }
                }
            }
        }
    }
}
