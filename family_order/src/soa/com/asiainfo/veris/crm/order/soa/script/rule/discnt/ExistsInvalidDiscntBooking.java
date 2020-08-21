
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 对不起，该优惠不能预约
 * @author: xiaocl
 */

/*
 * SELECT count(1) recordcount FROM tf_b_trade_discnt a WHERE a.trade_id=TO_NUMBER(:TRADE_ID) AND
 * a.accept_month=TO_NUMBER(:ACCEPT_MONTH) AND a.modify_tag = '0' AND a.user_id = (SELECT user_id FROM tf_b_trade WHERE
 * trade_id = TO_NUMBER(:TRADE_ID)) AND EXISTS (SELECT 1 FROM tf_b_trade c WHERE c.trade_id = TO_NUMBER(:TRADE_ID) AND
 * SUBSTR(c.process_tag_set,19,1) = '1') AND NOT EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code = 'CSM' AND
 * b.param_attr = 243 AND b.param_code = '0' AND TRIM(b.para_code1) = a.discnt_code AND SYSDATE < b.end_date AND
 * b.eparchy_code = :EPARCHY_CODE)
 */

public class ExistsInvalidDiscntBooking extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(ExistsInvalidDiscntBooking.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsInvalidDiscntBooking() >>>>>>>>>>>>>>>>>>");

		/* 自定义区域 */
		boolean bResult = false;
		IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
		IDataset listTrade = databus.getDataset("TF_B_TRADE");
		String strEparchyCode = databus.getString("EPARCHY_CODE");
		IDataset listPreDiscnt = BreQryForCommparaOrTag.getCommparaCode1("CSM", 243, "0", strEparchyCode);
		String discnt_name = "";

		// 先判断此条优惠台账是否属于预约优惠变更的台账
		for (Iterator iterDiscnt = listTradeDiscnt.iterator(); iterDiscnt.hasNext();)
		{
			IData tradeDiscnt = (IData) iterDiscnt.next();
			if (!BofConst.MODIFY_TAG_ADD.equals(tradeDiscnt.getString("MODIFY_TAG")))
			{
				continue;
			}
			for (Iterator iter = listTrade.iterator(); iter.hasNext();)
			{
				IData trade = (IData) iter.next();
				if (tradeDiscnt.getString("TRADE_ID").equals(trade.getString("TRADE_ID")) && trade.getString("PROCESS_TAG_SET").substring(18, 19).equals("1"))
				{
					// 再判断此优惠是否存在预约优惠结果集中
					for (int iListPreDiscnt = 0, iSize = listPreDiscnt.size(); iListPreDiscnt < iSize; iListPreDiscnt++)
					{
						if (listPreDiscnt.getData(iListPreDiscnt).getString("PARA_CODE1").equals(tradeDiscnt.getString("DISCNT_CODE")) && BofConst.MODIFY_TAG_ADD.equals(tradeDiscnt.getString("MODIFY_TAG")))
						{
							bResult = false;
							break;
						}
						else
						{
							bResult = true;
							continue;
						}
					}
					
					if(bResult){
						IData discnt = DiscntInfoQry.getDiscntInfoByCode2(tradeDiscnt.getString("DISCNT_CODE"));
						if(IDataUtil.isNotEmpty(discnt)){
							discnt_name = tradeDiscnt.getString("DISCNT_CODE") + ":" + discnt.getString("DISCNT_NAME");
						}else{
							discnt_name = tradeDiscnt.getString("DISCNT_CODE");
						}
				        StringBuilder strbError = new StringBuilder();
				         
				        strbError.append("业务登记前条件判断:该优惠").append(discnt_name).append("不能预约!");
				        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2014120801, strbError.toString());
				    }
				}
				if (!bResult)
				{
					break;
				}
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsInvalidDiscntBooking() " + bResult + "<<<<<<<<<<<<<<<<<<<");

		return bResult;
	}

}
