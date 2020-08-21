
package com.asiainfo.veris.crm.order.soa.person.rule.run.nophonewide;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
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
 *  IS_OPEN_CYCLE 合帐期间是否可以办理  ，老系统都给的1，不能办理
 * 
 * @author Mr.Z
 */
public class CheckUserBalance extends BreBase implements IBREScript
{
	private static final long serialVersionUID = 6702192869775312603L;

	private static Logger logger = Logger.getLogger(CheckUserBalance.class);

	public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserBalance() >>>>>>>>>>>>>>>>>>");
		}

		String isOpenCycle = ruleparam.getString(databus, "IS_OPEN_CYCLE");

		if ("1".equals(isOpenCycle))
		{
			String acctDay = databus.getString("ACCT_DAY");
			String firstDay = databus.getString("FIRST_DATE");
			String nextAcctDay = databus.getString("NEXT_ACCT_DAY");
			String nextFistDate = databus.getString("NEXT_FIRST_DATE");
			String startDate = databus.getString("ACCT_START_DATE");
			String nextStartDate = databus.getString("NEXT_START_DATE");
			SaleActiveUtil.setAcctDayInfo(acctDay, firstDay, nextAcctDay, nextFistDate, startDate, nextStartDate);
			String lastMothLastDate = SysDateMgr.getLastDateThisMonth();
			lastMothLastDate = SysDateMgr.date2String(DateUtils.addMonths(SysDateMgr.string2Date(lastMothLastDate, "yyyy-MM-dd"), -1), "yyyy-MM-dd");
			String userID=databus.getString("USER_ID");
			boolean tag = !BreQry.isOpenCycle(userID, lastMothLastDate);

			if (tag)
			{
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2014062703, "非常抱歉，合账期间，暂不能办理该业务，请您稍后办理 ，敬请谅解！");
				return false;
			}
		}

		if (logger.isDebugEnabled())
		{
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckUserBalance() >>>>>>>>>>>>>>>>>>");
		}

		return true;
	}
}
