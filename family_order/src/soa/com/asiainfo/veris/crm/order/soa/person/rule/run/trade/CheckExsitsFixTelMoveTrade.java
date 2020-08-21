
package com.asiainfo.veris.crm.order.soa.person.rule.run.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 校验是否有固话移机未完工单
 * 
 * @author longtian
 * @date 2014-06-07
 */
public class CheckExsitsFixTelMoveTrade extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String tradeTypeCode = "9703";
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IDataset result = TradeInfoQry.getMainTradeBySN(serialNumber, tradeTypeCode);
        if (IDataUtil.isNotEmpty(result))
        {
            if (StringUtils.isNotBlank(result.getData(0).getString("RSRV_STR1")))
            {
                databus.put("strSerialNumber", serialNumber);
                return true;
            }
        }
        return false;
    }

}
