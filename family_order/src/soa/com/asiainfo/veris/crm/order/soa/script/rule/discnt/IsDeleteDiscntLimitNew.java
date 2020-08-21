
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: F0包月优惠累计生效30天后才可以办理变更 F0包月优惠累计生效(2、3月份28,其余30)天(奥运588、888、1688套餐为180天)后才可以办理变更！
 *               F0包月优惠累计生效(2、3月份28,其余30)天后才可以办理变更，请修改产品预约时间！
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a WHERE a.trade_id = TO_NUMBER(:TRADE_ID) AND a.accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND a.modify_tag = '1' AND EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code = 'CSM'
 * AND b.param_attr = 355 AND TO_NUMBER(TRIM(b.param_code)) = a.discnt_code AND ( (TO_NUMBER(:ACCEPT_MONTH) not in (2,3)
 * AND a.end_date-a.start_date < TO_NUMBER(b.para_code1)) OR (TO_NUMBER(:ACCEPT_MONTH) in (2,3) AND
 * a.end_date-a.start_date < TO_NUMBER(b.para_code4)) ) AND SYSDATE < b.end_date AND b.eparchy_code = :EPARCHY_CODE) AND
 * EXISTS (SELECT 1 FROM tf_f_user c WHERE c.user_id = a.user_id AND c.acct_tag = '0')
 */
public class IsDeleteDiscntLimitNew extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(IsDeleteDiscntLimitNew.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsDeleteDiscntLimitNew() >>>>>>>>>>>>>>>>>>");

		boolean bResult = false;
		boolean bExistsOne = false; // 设置第一逻辑点
		boolean bExistsTwo = false; // 设置第二逻辑点

		IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset listUser = databus.getDataset("TF_F_USER");
		if (IDataUtil.isEmpty(listTradeDiscnt))
		{
			return false;
		}
		String strAcceptMonth = listTradeDiscnt.first().getString("ACCEPT_MONTH");
		String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
		IDataset ComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 355, strEparchyCode);
		if (IDataUtil.isEmpty(ComparaInfo))
		{
			return false;
		}

		String sysdate = SysDateMgr.getSysDate();
		for (int iListTradeDiscnt = 0, iASize = listTradeDiscnt.size(); iListTradeDiscnt < iASize; iListTradeDiscnt++)
		{
			IData tradeData = listTradeDiscnt.getData(iListTradeDiscnt);
			for (int iListUser = 0; iListUser < listUser.size(); iListUser++)
			{
				IData userData = listUser.getData(iListUser);
				if (tradeData.getString("USER_ID").equals(userData.getString("USER_ID")) && "0".equals(userData.getString("ACCT_TAG")) && tradeData.getString("MODIFY_TAG").equals("1"))
				{

					bExistsOne = true;
					break;
				}
			}

			for (int iComparaInfo = 0, iBSize = ComparaInfo.size(); iComparaInfo < iBSize; iComparaInfo++)
			{
				IData comparaInfo = ComparaInfo.getData(iComparaInfo);
				if (comparaInfo.getString("PARAM_CODE").equals(tradeData.getString("DISCNT_CODE"))
						&& ((!strAcceptMonth.equals("02") && !strAcceptMonth.equals("03") && 
						(SysDateMgr.dayInterval(tradeData.getString("END_DATE"), tradeData.getString("START_DATE")) < comparaInfo.getInt("PARA_CODE1")))
						|| ((strAcceptMonth.equals("02") || strAcceptMonth.equals("03"))&& SysDateMgr.dayInterval(tradeData.getString("END_DATE"), tradeData.getString("START_DATE")) < comparaInfo.getInt("PARA_CODE4"))))
				{
					// BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
					// "201119","[date1]"+tradeData.getString("END_DATE")+"[date2]"+tradeData.getString("START_DATE")+"[end]"+tradeData.getString("END_DATE").compareTo(tradeData.getString("START_DATE")));
					String startDate = tradeData.getString("START_DATE");
					String endDate = tradeData.getString("END_DATE");
					if (startDate.compareTo(sysdate) > 0 && endDate.compareTo(startDate) < 0)
					{
						continue;
					}
					bExistsTwo = true;
					break;
				}
			}

			if (bExistsTwo && bExistsOne)
			{
				bResult = true;
				break;
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsDeleteDiscntLimitNew() " + bResult + "<<<<<<<<<<<<<<<<<<<");

		return bResult;
	}
}
