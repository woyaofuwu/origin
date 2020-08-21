package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckSchoolGpsRuleA extends BreBase implements IBREScript 
{

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		IDataset listTradeDiscnts = databus.getDataset("TF_B_TRADE_DISCNT");
		
		if(IDataUtil.isNotEmpty(listTradeDiscnts))
		{
			Integer nAddDCCount = 0;
			for(int i=0, size=listTradeDiscnts.size(); i<size; i++)
			{
				IData listTradeDiscnt = listTradeDiscnts.getData(i);
				String strModifyTag = listTradeDiscnt.getString("MODIFY_TAG");
				if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
				{
					String strUserId = listTradeDiscnt.getString("USER_ID");
					String strAdc = listTradeDiscnt.getString("DISCNT_CODE");
					String strAddEndDate = listTradeDiscnt.getString("END_DATE");
					IDataset checkDats = CommparaInfoQry.getCommparaInfoByCode("CSM", "1417", "SCHOOL_GPS_DISCNT", strAdc, "0898");
					
					if(IDataUtil.isNotEmpty(checkDats))
					{
						IData checkRule = checkDats.getData(0);
						String discntName = checkRule.getString("PARA_CODE2");
						//String beginLimitTime = checkRule.getString("PARA_CODE3");
						//String endLimitTime = checkRule.getString("PARA_CODE4");
						String strLastDateThisMonth = SysDateMgr.getLastDateThisMonth();
						if(strAddEndDate.compareTo(strLastDateThisMonth) > 0)
						{
							nAddDCCount = nAddDCCount + 1;
						}
						
						IDataset Commparas1417 = CommparaInfoQry.getCommNetInfo("CSM", "1417", "SCHOOL_GPS_DISCNT");
						if(IDataUtil.isNotEmpty(Commparas1417))
						{
							for (int j = 0; j < Commparas1417.size(); j++) 
							{
								IData Commpara1417 = Commparas1417.getData(j);
								String strPdc = Commpara1417.getString("PARA_CODE1", "");
								IDataset DiscntLimits = UserDiscntInfoQry.queryUserDiscntV(strUserId, strPdc);
								if(IDataUtil.isNotEmpty(DiscntLimits))
								{
									for (int k = 0; k < DiscntLimits.size(); k++)
									{
										IData DiscntLimit = DiscntLimits.getData(k);
										String strElementId = DiscntLimit.getString("DISCNT_CODE", "");
										String strelementName = UDiscntInfoQry.getDiscntNameByDiscntCode(strElementId);
										String strUed = DiscntLimit.getString("END_DATE", "");
										
										boolean bIsSame = false;
										for (int n = 0; n < listTradeDiscnts.size(); n++) 
										{
											IData idTD = listTradeDiscnts.getData(n);
											String strTDdc = idTD.getString("DISCNT_CODE");
											String strTDmt = idTD.getString("MODIFY_TAG");
											if("1".equals(strTDmt) && strTDdc.equals(strElementId))
											{
												bIsSame = true;
												break;
											}
										}
										
										if(strUed.compareTo(strLastDateThisMonth) > 0 && !bIsSame)
										{
											BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525019, "尊敬的用户，您好！请先取消已办理优惠" + strelementName + "(" + strElementId + ")，才能办理优惠" + discntName + "(" + strAdc + ")");
				                            return true;
										}
									}
								}
							}
						}
						
						if(nAddDCCount >= 2)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525019, "尊敬的用户，您好！不能同时办理多个校内流量包。");
                            return true;
						}
					}
				}
			}
		}
		return false;
    }
	
}
