package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

/**
 * 携转号码不能办理魔百和开户，相当于前移了CheckNpCodeByPlatSvcAction规则
 */
public class CheckNpForInternetTvOpen extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER");
    	IDataset ids = TradeNpQry.getValidTradeNpBySn(serialNumber);
    	if (IDataUtil.isNotEmpty(ids))
        {
    		String asp = ids.getData(0).getString("ASP", "").trim();
    		if("2".equals(asp) || "3".equals(asp))
    		{
    			return true;
    		}
        }

        return false;
    }
}
