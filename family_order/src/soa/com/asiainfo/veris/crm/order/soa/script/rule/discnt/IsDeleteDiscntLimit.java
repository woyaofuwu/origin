
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
 * @Description: 包月优惠累计生效30天后才可以办理变更！ *
 * @author: xiaocl
 */
/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a WHERE a.trade_id = TO_NUMBER(:TRADE_ID) AND a.accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND a.modify_tag = '1' AND EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code = 'CSM'
 * AND b.param_attr = 355 AND TO_NUMBER(TRIM(b.param_code)) = a.discnt_code AND a.end_date-a.start_date <
 * TO_NUMBER(b.para_code1) AND SYSDATE < b.end_date AND b.eparchy_code = :EPARCHY_CODE)
 */
public class IsDeleteDiscntLimit extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(IsDeleteDiscntLimit.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsDeleteDiscntLimit() >>>>>>>>>>>>>>>>>>");

		boolean bResult = false;

		IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
		String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
		IDataset ComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 355, strEparchyCode);
		if (IDataUtil.isEmpty(ComparaInfo) || IDataUtil.isEmpty(listTradeDiscnt))
		{
			return false;
		}

		String sysdate = SysDateMgr.getSysDate();
		for (int iListTradeDiscnt = 0, iASize = listTradeDiscnt.size(); iListTradeDiscnt < iASize; iListTradeDiscnt++)
		{
			IData tradeData = listTradeDiscnt.getData(iListTradeDiscnt);
			for (int iComparaInfo = 0, iBSize = ComparaInfo.size(); iComparaInfo < iBSize; iComparaInfo++)
			{
				IData comparaInfo = ComparaInfo.getData(iComparaInfo);
				if (tradeData.getString("DISCNT_CODE").equals(comparaInfo.getString("PARAM_CODE").trim()) && tradeData.getString("MODIFY_TAG").equals("1")
						&& (tradeData.getString("END_DATE").compareTo(tradeData.getString("START_DATE")) < comparaInfo.getInt("PARA_CODE1")))
				{
					String startDate = tradeData.getString("START_DATE");
					String endDate = tradeData.getString("END_DATE");
					if (startDate.compareTo(sysdate) > 0 && endDate.compareTo(startDate) < 0)
					{
						continue;
					}
					bResult = true;
				}
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsDeleteDiscntLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");
		return bResult;
	}
}
