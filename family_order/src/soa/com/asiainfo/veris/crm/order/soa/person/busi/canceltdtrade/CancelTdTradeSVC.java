/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.canceltdtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelTdTradeSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-8-4 下午04:01:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-4 chengxf2 v1.0.0 修改原因
 */

public class CancelTdTradeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-4 下午04:01:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-4 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryCancelTrade(IData pdData) throws Exception
    {
        String serialNumber = pdData.getString("SERIAL_NUMBER", "");
        String tradeTypeCode = pdData.getString("TRADE_TYPE_CODE", "");
        String startDate = pdData.getString("START_DATE", "");
        String endDate = pdData.getString("END_DATE", "");
        IDataset tradeInfos = new DatasetList();
        // 查询用户信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            IData userInfoData = userInfo;
            String userId = userInfoData.getString("USER_ID", "0");

            String qryTradeEaprchyCode = getVisit().getStaffEparchyCode();
            tradeInfos = TradeHistoryInfoQry.queryCanBackTradeBySnAndTypeCodeAndDate(userId, tradeTypeCode, qryTradeEaprchyCode, startDate, endDate);
        }
        return tradeInfos;
    }

}
