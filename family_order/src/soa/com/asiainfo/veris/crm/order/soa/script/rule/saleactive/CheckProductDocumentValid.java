package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 
 * 验证用户的证件是否能够办理营销活动
 *
 */
public class CheckProductDocumentValid extends BreBase implements IBREScript{

	private static final long serialVersionUID = 2987559940602315670L;
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		String saleProductId = databus.getString("PRODUCT_ID");
		
		IDataset docRules=CommparaInfoQry.queryComparaByAttrAndCode1
				("CSM", "5212", "SALE_ACTIVE_DOC", saleProductId);
		
		if(IDataUtil.isNotEmpty(docRules)){
			IData docRule=docRules.getData(0);
			
			String userId = databus.getString("USER_ID");
			String serialNumber=databus.getString("SERIAL_NUMBER");
			IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
			
			//用户信息为空，说明是从开户那边进入
			if(IDataUtil.isEmpty(userInfo)){
				return false;
			}
			
			
			String custId=userInfo.getString("CUST_ID");
			
			IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
			String psptTypeCode=custInfo.getString("PSPT_TYPE_CODE","");
			String psptId=custInfo.getString("PSPT_ID","");
			
			
			String docTypeRange=docRule.getString("PARA_CODE2","");
			//增加办理次数判断，即每张身份证/护照/户口本/军官证名下的手机号码，仅允许有3个手机号码办理上述“和流量包”。
			//其中，证件号码一致但类型不一致的情况，视为同一客户证件（即过滤同时存在本地身份证、外地身份证的客户）。  
			String docTypeCount=docRule.getString("PARA_CODE3","0");
			int sumcout = 0;
			
			/*
			 * 如果没有配置证件类型，那么只要与用户的证件类型和证件号相同，就需要验证
			 * 如果配置了，就需要验证配置的证件类型是否包含用户的证件类型
			 *  
			 */
			
			if(!docTypeRange.trim().equals("")){
				String[] docTypeRangeArr=docTypeRange.split("|");
				boolean isNeedVerify=false;
				
				for(int i=0,size=docTypeRangeArr.length;i<size;i++){
					if(docTypeRangeArr[i]!=null&&!docTypeRangeArr[i].trim().equals("")){
						if(docTypeRangeArr[i].equals(psptTypeCode)){
							isNeedVerify=true;							
							break;
						}
					}
				}
				
				if(!isNeedVerify){
					String psptTypeName=StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", psptTypeCode);
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"14087624", "本活动不允许"+psptTypeName+"用户办理！");
					return true;
				}else{
					//新增判断 begin
					IDataset userIdDatas=UserInfoQry.querySameDocumentUserIdsByPsptId(psptId);
					if(IDataUtil.isNotEmpty(userIdDatas)){
						for(int j=0;j<userIdDatas.size();j++){
							IData userData=userIdDatas.getData(j);
							String userIdOther=userData.getString("USER_ID");
							if(!userId.equals(userIdOther)){
								IDataset discntDataOs=UserSaleActiveInfoQry.queryUserSaleActiveProdId(userIdOther, saleProductId,"0");
								if(IDataUtil.isNotEmpty(discntDataOs)){
									sumcout=sumcout+1;
								}
								if(sumcout > Integer.parseInt(docTypeCount))
								{
									BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"20161214", "持有用户相同证件不满足此营销活动办理！");
									return true;
								}
							}else{
								sumcout=sumcout+1;
							}
						}
					}
					//新增判断 end
				}
				
			}	
				
				
			/*
			 * 获取用户的证件对应的用户信息
			 * 然后验证这些用户是否办理了产品的相关套餐信息
			 */
			if(!psptTypeCode.equals("")&&!psptId.equals("") && sumcout ==0){
				IDataset userIdDatas=UserInfoQry.querySameDocumentUserIds(psptTypeCode, psptId);
				
				if(IDataUtil.isNotEmpty(userIdDatas)){
					for(int i=0,size=userIdDatas.size();i<size;i++){
						IData userData=userIdDatas.getData(i);
						
						String userIdOther=userData.getString("USER_ID");
						if(!userId.equals(userIdOther)){
							IDataset discntDataOs=UserSaleActiveInfoQry.queryUserSaleActiveProdId(userIdOther, saleProductId,"0");
							if(IDataUtil.isNotEmpty(discntDataOs)){
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"14087623", "持有用户相同证件的其他用户已经开通了此营销活动！");
								return true;
							}
						}
					}
				}
			}
		}
		
		
		return false;
    }
}
