
package com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 买断手工激活返销
 * 
 * @author sunxin
 */
public class UndoSaleCardOpenRegSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(UndoSaleCardOpenRegSVC.class);

    /**
     * 买断手工激活返销
     */
    public static IDataset UndoSaleCardOpenReg(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        String tradeId = "";
        IDataset tradeInfo = TradeInfoQry.getMainTradeBySN(sn, "14");
        if (IDataUtil.isEmpty(tradeInfo))
        {
            IDataset tradeHisInfo = TradeHistoryInfoQry.queryTradeHisInfo(sn, "14", "0");
            if (IDataUtil.isEmpty(tradeHisInfo))
                CSAppException.apperr(TradeException.CRM_TRADE_19);
			else
			{
				tradeId = tradeHisInfo.getData(0).getString("TRADE_ID");
				IDataset userInfo = UserInfoQry.getAllUserInfoBySn(sn);
				if (IDataUtil.isNotEmpty(userInfo))
				{
					String openMode = userInfo.getData(0).getString("OPEN_MODE");
					if (!"2".equals(openMode))
						CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是预开已返单");
				}
			}
		} else
		{
			tradeId = tradeInfo.getData(0).getString("TRADE_ID");
			IDataset userInfo = UserInfoQry.getAllUserInfoBySn(sn);
			if (IDataUtil.isNotEmpty(userInfo))
			{
				String openMode = userInfo.getData(0).getString("OPEN_MODE");
				if (!"2".equals(openMode))
					CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码不是预开已返单");
			}
		}
        IData inData = new DataMap();
        inData.put("TRADE_ID", tradeId);
        inData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        return CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", inData);
    }

}
