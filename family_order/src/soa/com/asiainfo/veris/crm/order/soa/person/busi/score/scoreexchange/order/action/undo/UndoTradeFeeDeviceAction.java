
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;

public class UndoTradeFeeDeviceAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeFeeDs = TradeFeeDeviceQry.queryTradeFeeDeviceByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeFeeDs))
            return;
        int size = tradeFeeDs.size();
        for (int i = 0; i < size; i++)
        {
            IData card = tradeFeeDs.getData(i);
            IDataset resSet = ResCall.updateValueCardReturnInfoIntf(card.getString("DEVICE_NO_S"), card.getString("DEVICE_NO_E"), "3", CSBizBean.getVisit().getDepartId(), "0", null, null, null, null, "330");
            if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
            {
                // THROW_C(216201, "调用资源更新接口出错！");
                CSAppException.apperr(CrmUserException.CRM_USER_867);
            }
        }
    }

}
