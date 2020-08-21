package com.asiainfo.veris.crm.order.soa.person.rule.run.relationuu;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 返销385业务（多人约消注销）时，如果存在新的组网关系，则不允许返销。
 * @author Cnaic
 *
 */
public class CheckRalationUUBRE extends BreBase implements IBREScript
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		IData tradeInfo = new DataMap(databus.getString("TRADE_INFO"));
		
		String sn = tradeInfo.getString("SERIAL_NUMBER");
		IDataset mainSn = RelaUUInfoQry.queryRelaUUBySnb(sn,"61");
		
		if(IDataUtil.isNotEmpty(mainSn))
		{
			return true;
		}
		return false;
	}
	
}