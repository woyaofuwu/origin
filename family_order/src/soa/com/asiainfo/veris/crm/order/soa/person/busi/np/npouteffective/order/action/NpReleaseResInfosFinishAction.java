
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpReleaseResInfosAction.java
 * @Description: NP释放资源
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-24 下午2:34:06 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-24 lijm3 v1.0.0 修改原因
 */
public class NpReleaseResInfosFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset reses = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, "1");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");

        String simCode = "";
        String sn = "";
        // 携出生效号码跟sim卡都要释放
        if (IDataUtil.isNotEmpty(reses))
        {
            for (int i = 0, len = reses.size(); i < len; i++)
            {
                IData data = reses.getData(i);
                String resTypeCode = data.getString("RES_TYPE_CODE");
                String resCode = data.getString("RES_CODE");
                if ("0".equals(resTypeCode))
                {
                    // 号码资源
                    // RM.ResPhoneIntfSvc.mphoneDestroy
                    sn = resCode;
                }
                else if ("1".equals(resTypeCode))
                {
                    // sim卡资源
                    // RM.ResSimCardIntfSvc.simCardDestroy
                    simCode = resCode;

                }
            }
        }

        if (StringUtils.isNotBlank(simCode))
        {
            ResCall.modifyNpSimInfo(simCode, "1", serialNumber, tradeId, userId);

        }
        if (StringUtils.isNotBlank(sn) && StringUtils.isNotBlank(simCode))
        {
            ResCall.modifyNpMphoneInfo(simCode, "1", sn);
        }

    }

}
