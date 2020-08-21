
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelin.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: InNpApplyCancelFinishAction.java
 * @Description: 携入取消，需要把携入申请时资源给释放
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-19 上午9:14:43 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-19 lijm3 v1.0.0 修改原因
 */
public class InNpApplyCancelFinishAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        // 修改sim息息
        String tradeId = mainTrade.getString("RSRV_STR1");// 得到携入开户的tradeId
        IDataset ids = TradeResInfoQry.getTradeRes(tradeId, "1", "0");
        if (IDataUtil.isNotEmpty(ids))
        {
            String res_code = ids.getData(0).getString("RES_CODE");
            ResCall.modifyNpSimInfo(res_code, "6", "", "", "");

        }
        else
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "115001 获取TRADE_ID=[" + tradeId + "]的SIM卡资料异常！");
        }
    }
}
