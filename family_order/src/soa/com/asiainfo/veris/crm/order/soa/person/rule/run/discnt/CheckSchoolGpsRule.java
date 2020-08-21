package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CheckSchoolGpsRule extends BreBase implements IBREScript 
{

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		IDataset listTradeDiscnts = databus.getDataset("TF_B_TRADE_DISCNT");
		
		if(IDataUtil.isNotEmpty(listTradeDiscnts))
		{
			for(int i=0, size=listTradeDiscnts.size(); i<size; i++)
			{
				IData listTradeDiscnt = listTradeDiscnts.getData(i);
				String strModifyTag = listTradeDiscnt.getString("MODIFY_TAG");
				if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
				{
					String strUserId = listTradeDiscnt.getString("USER_ID");
					String strAdc = listTradeDiscnt.getString("DISCNT_CODE");
					IDataset checkDats = CommparaInfoQry.getCommparaInfoByCode("CSM", "1417", "SCHOOL_GPS_DISCNT", strAdc, "0898");
					
					if(IDataUtil.isNotEmpty(checkDats))
					{
						IData checkRule = checkDats.getData(0);
						String discntName = checkRule.getString("PARA_CODE2");
						String beginLimitTime = checkRule.getString("PARA_CODE3");
						String endLimitTime = checkRule.getString("PARA_CODE4");
						
						int validResult = isValid(strUserId, beginLimitTime, endLimitTime);
						
						if(validResult == 1)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525018, "非实名制用户无法办理优惠" + discntName + "(" + strAdc + ")");
                            return true;
						}
						else if(validResult == 2)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525018, "用户证件类型不为身份证或户口本，无法办理优惠" + discntName + "(" + strAdc + ")");
                            return true;
						}
						else if(validResult == 3)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525018,
									"用户的身份证或户口本生日必须是在"+SysDateMgr.decodeTimestamp(beginLimitTime, SysDateMgr.PATTERN_STAND)+"至" +
											SysDateMgr.decodeTimestamp(endLimitTime, SysDateMgr.PATTERN_STAND)+		
									"区间之内,才能办理优惠"+discntName+"(" + strAdc + ")");
							return true;
						}
						else if(validResult == 4)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525018,
									"用户的身份证或户口本生日必须是大于等于"+SysDateMgr.decodeTimestamp(beginLimitTime, SysDateMgr.PATTERN_STAND)+	
									",才能办理优惠"+discntName+"(" + strAdc + ")");
							return true;
						}
						else if(validResult == 5)
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525018,
									"用户的身份证或户口本生日必须是小于等于" + SysDateMgr.decodeTimestamp(endLimitTime, SysDateMgr.PATTERN_STAND)+		
									",才能办理优惠" + discntName + "(" + strAdc + ")");
							return true;
						}
					}
				}
			}
		}
		
		return false;
    }
	
	
	private int isValid(String userId, String beginLimitTime, String endLimitTime)throws Exception
	{
		//非实名制是不能办理的
		//非身份证也是不能办的
		//还需要进行年龄限制
		
		IData userData=UcaInfoQry.qryUserInfoByUserId(userId);
		String custId=userData.getString("CUST_ID");
		IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
		
		/*
		 * 验证是否是实名制
		 */
		String isRealName=custInfo.getString("IS_REAL_NAME","");
		if(!(isRealName.equals("1")))
		{
			return 1;
		}
		
		/*
		 * 验证是否为身份证
		 */
		String psptTypeCode=custInfo.getString("PSPT_TYPE_CODE","");
		String psptId=custInfo.getString("PSPT_ID","");
		if(!(psptTypeCode.equals("0") || psptTypeCode.equals("1") || psptTypeCode.equals("2")))
		{
			return 2;
		}
		
		/*
		 * 验证年龄限制
		 */
		String birthday=obtainBirthdayYearFromDoc(psptId);
		if(beginLimitTime!=null && !beginLimitTime.equals("") && endLimitTime!=null&&!endLimitTime.equals(""))
		{
			if(!(birthday.compareTo(beginLimitTime)>=0 && birthday.compareTo(endLimitTime)<=0))
			{
				return 3;
			}
		}
		else if(beginLimitTime!=null && !beginLimitTime.equals("") && (endLimitTime==null || endLimitTime.equals("")))
		{
			if(!(birthday.compareTo(beginLimitTime) > 0))
			{
				return 4;
			}
			
		}
		else if((beginLimitTime==null || beginLimitTime.equals("")) && (endLimitTime!=null && !endLimitTime.equals("")))
		{
			if(!(birthday.compareTo(endLimitTime) < 0))
			{
				return 5;
			}
		}
		
		return 0;
		
	}
	
	private String obtainBirthdayYearFromDoc(String psptId) throws Exception
	{
		String tmpStr="";
		
		if(psptId.length()==15) 
		{
			tmpStr= "19" + psptId.substring(6,12);
		}
		else 
		{
	    	tmpStr = psptId.substring(6,14);
		}
		
		return tmpStr.substring(0,4) + tmpStr.substring(4,6) + tmpStr.substring(6);
	}
	
	
}
