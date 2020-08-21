
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 选包\提交后都需要校验 配置在通用参数的 144 拆分出来, TD_BRE_PARAMETER 传入 LIMIT_LEAVE_FEE 限制金额 IS_OPEN_CYCLE 合帐期间是否可以办理 ，老系统都给的1，不能办理
 * 
 * @author Mr.Z
 */
public class CheckUserLeaveFee extends BreBase implements IBREScript
{

	private static final long serialVersionUID = -3623381596735839361L;

	private static Logger logger = Logger.getLogger(CheckUserLeaveFee.class);

	public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserLeaveFee() >>>>>>>>>>>>>>>>>>");
		}

		String isOpenCycle = ruleparam.getString(databus, "IS_OPEN_CYCLE");

		if ("1".equals(isOpenCycle))
		{
			IData param = new DataMap();

			param.put("ACCT_DAY", databus.getString("ACCT_DAY"));
			param.put("FIRST_DATE", databus.getString("FIRST_DATE"));
			param.put("NEXT_ACCT_DAY", databus.getString("NEXT_ACCT_DAY"));
			param.put("NEXT_FIRST_DATE", databus.getString("NEXT_FIRST_DATE"));

			SaleActiveUtil.setAcctDayInfo(param);

			String lastMothLastDate = SysDateMgr.getLastDateThisMonth();
			lastMothLastDate = SysDateMgr.date2String(DateUtils.addMonths(SysDateMgr.string2Date(lastMothLastDate, "yyyy-MM-dd"), -1), "yyyy-MM-dd");

			boolean tag = !BreQry.isOpenCycle(databus.getString("USER_ID"), lastMothLastDate);

			if (tag)
			{
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2014062703, "非常抱歉，合账期间，暂不能办理营销活动，请您稍后办理 ，敬请谅解！");
				return false;
			}
		}

		if (logger.isDebugEnabled())
		{
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckUserLeaveFee() >>>>>>>>>>>>>>>>>>");
		}

		return true;
	}

}
