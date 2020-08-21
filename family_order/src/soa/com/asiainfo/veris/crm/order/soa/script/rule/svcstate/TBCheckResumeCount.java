package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class TBCheckResumeCount extends BreBase implements IBREScript
{

	private static Logger logger = Logger.getLogger(TBCheckResumeCount.class);

	/**
	 * Copyright: Copyright 2016 Asiainfo-Linkage
	 * 
	 * @Description: 紧急开机次数判断 【TradeCheckBefore】
	 * @author: wujy3
	 */

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckResumeCount() >>>>>>>>>>>>>>>>>>");
		boolean bResult = false;

		boolean haveRight = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "UNLIMIT_EMGENCYOPEN");
		if (haveRight)
			return bResult;

		String userId = databus.getString("USER_ID");
		IData userCreditInfo = CreditCall.queryUserCreditInfos(userId);
		String creditClass = userCreditInfo.getString("CREDIT_CLASS", "-1");
		String openCounts = "";
		IDataset infos = CommparaInfoQry.queryCommparaByCodeAndName("497", "EOSC", creditClass, "CSM", "4900");
		if (IDataUtil.isNotEmpty(infos))
		{
			openCounts = infos.getData(0).getString("PARA_CODE3");
			IData param = new DataMap();
			param.put("USER_ID", userId);
			IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_EO_COUNT_BY_UID", param, Route.getJourDb());
			String rowNum = "0";
			if (IDataUtil.isNotEmpty(dataset))
			{
				rowNum = dataset.getData(0).getString("ROW_NUM", "0");
				if (Integer.parseInt(rowNum) >= Integer.parseInt(openCounts))
				{
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "160307", "当月已办理" + rowNum + "次紧急开机业务，不允许再办理。");
					return true;
				}
			}
		}

		return bResult;
	}

}
