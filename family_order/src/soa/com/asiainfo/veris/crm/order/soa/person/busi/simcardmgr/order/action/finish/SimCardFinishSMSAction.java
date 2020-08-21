
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 如果使用4G卡，没有订购4G优惠，则下发短信
 */
public class SimCardFinishSMSAction implements ITradeFinishAction
{
	public void executeAction(IData mainTrade) throws Exception
	{
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
		
		//判断用户是否是4G卡
		IData param=new DataMap();
		param.put("USER_ID", userId);
		IDataset resSet = Dao.qryByCode("TF_F_USER_RES", "SEL_USER_CARD_IS_4G", param, Route.CONN_CRM_CG);
		
		
		boolean isSendPromoteSms=false;
		
		if(resSet!=null&&!resSet.isEmpty()){
			//判断用户是否订购了相关4G套餐
			IDataset the4GDiscnt=Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_SUBSCRIBE_THE_4G_DISCNT", param, Route.CONN_CRM_CG);
			
			//没有订购套餐
			if(!(the4GDiscnt!=null&&the4GDiscnt.size()>0)){
//				//判断用户现在的套餐是否和4G套餐存在互斥
//				IDataset limitDiscnt=Dao.qryByCode("TD_B_ELEMENT_LIMIT", "CHECK_IS_EXISTS_LIMIT", param, Route.CONN_CRM_CG);
//				int limitNum=limitDiscnt.getData(0).getInt("TOTAL_NUM", 0);
//				
//				if(limitNum<=0){	//如果不存在互斥，就发送推荐短信
//					isSendPromoteSms=true;
//				}
				
				//isSendPromoteSms=true;
				isSendPromoteSms=false;
			}
		}
		
		StringBuilder smsContent=new StringBuilder();

		if(isSendPromoteSms){	//推送4G营销短信
			//获取推送短信内容
			IDataset smsConfig = CommparaInfoQry.getCommparaByCode1("CSM", "0", "SMS_CONTENT_TEMPLATE", "CHANGE_CARD_SMS_CONTENT_TEMPLATE", null);
			if(smsConfig!=null&&smsConfig.size()>0){
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE2",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE3",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE4",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE5",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE6",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE7",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE8",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE9",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE10",""));
				smsContent.append(smsConfig.getData(0).getString("PARA_CODE11",""));
			}

		}else{
			String processTagSet=mainTrade.getString("PROCESS_TAG_SET","");
			
			//获取tagSet首字母
			String firstLetterOfTagSet="";
			if(processTagSet.trim().length()>1){
				firstLetterOfTagSet=processTagSet.substring(0, 1);
			}
			
			if(tradeTypeCode.equals("142")){	//换卡（写卡）
				
				//获取推送短信内容
				IDataset smsConfig = CommparaInfoQry.getCommparaByCode1("CSM", "0", "SMS_CONTENT_TEMPLATE", "CHANGE_CARD_SMS_CONTENT_TEMPLATE_142", null);

				if(firstLetterOfTagSet.equals("N")){
					if(smsConfig!=null&&smsConfig.size()>0){
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE2",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE3",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE4",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE5",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE6",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE7",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE8",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE9",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE10",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE11",""));
					}
				}else{
					if(smsConfig!=null&&smsConfig.size()>0){
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE2",""));
						smsContent.append("。");
					}
				}
				
			}else if(tradeTypeCode.equals("144")){	//大客户备卡激活
				//获取推送短信内容
				IDataset smsConfig = CommparaInfoQry.getCommparaByCode1("CSM", "0", "SMS_CONTENT_TEMPLATE", "CHANGE_CARD_SMS_CONTENT_TEMPLATE_144", null);
				
				if(firstLetterOfTagSet.equals("N")){
					if(smsConfig!=null&&smsConfig.size()>0){
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE2",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE3",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE4",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE5",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE6",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE7",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE8",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE9",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE10",""));
						smsContent.append(smsConfig.getData(0).getString("PARA_CODE11",""));
					}
				}
			}
		}
		
		//发送短信
		if(smsContent!=null&&smsContent.length()>0){
			IData smsData = new DataMap();
			smsData.put("RECV_OBJECT", serialNumber);
			smsData.put("NOTICE_CONTENT", smsContent.toString());
			SmsSend.insSms(smsData);
		}
		
	}

}
