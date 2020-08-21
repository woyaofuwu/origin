
package com.asiainfo.veris.crm.order.soa.person.rule.run.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 校验是否有宽带未完工单
 * 
 * @author chenzm
 * @date 2014-05-29
 */
public class CheckExsitsWidenetTrade extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String serialNumber = databus.getString("SERIAL_NUMBER");
        IDataset tradeInfos = TradeInfoQry.getTradeInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
            String tradeType = tradeTypeInfo.getString("TRADE_TYPE");
            String errorInfo = "该用户有" + tradeType + "未完工工单！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604019", errorInfo);
        }

        return false;

    }

}
