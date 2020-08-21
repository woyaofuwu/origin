
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetordercancel;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;

public class CttBroadbandOrderCancelSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-24 上午09:01:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-24 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryUserCancelTrade(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER");
        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
        IDataset dataset = WidenetTradeQuery.queryUserCancelTrade(serialNumber, tradeTypeCode);
        if (dataset.isEmpty())
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "没有查询到相关业务台帐,请确认输入的查询条件!");
        }
        IData resultData = dataset.getData(0);
        String tradeId = resultData.getString("TRADE_ID");
        IDataset tradeDiscntSet = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeDiscntSet))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_64, tradeId);
        }
        String discntCode = tradeDiscntSet.getData(0).getString("DISCNT_CODE");
        String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
        resultData.put("DISCNT_NAME", discntName);
        return dataset;
    }
}
