
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;

/**
 * @des 和生活积分兑换返销
 * @author huangsl
 */
public class ScoreCancel4PlatSVC extends CSBizService
{
    public IData scoreCancel (IData data) throws Exception
    {
        // 入参校验
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String tradeId = IDataUtil.chkParam(data, "TRADE_ID");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, data.getString("SERIAL_NUMBER"));
        }
        IDataset tradeHisDs = IDataUtil.idToIds(UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", this.getTradeEparchyCode()));
        if (IDataUtil.isEmpty(tradeHisDs))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_11);
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        
        // 调返销接口
        CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", data);
        IData returnData = new DataMap();
        returnData.put("X_RESULTCODE", "0");
        returnData.put("X_RESULTINFO", "返销登记成功！");
        return returnData;
    }
}
