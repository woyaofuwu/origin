package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.finish;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.goodsapply.GoodsApplyBean;

public class InserGoodsInfoSendSMSAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId=mainTrade.getString("TRADE_ID"); 
		String userId=mainTrade.getString("USER_ID"); 
		String serialNumber=mainTrade.getString("SERIAL_NUMBER");
		
		String exchangeStaffId=mainTrade.getString("UPDATE_STAFF_ID");
		String exchangeDepartId=mainTrade.getString("UPDATE_DEPART_ID");
		
		IDataset set=TradeScoreInfoQry.queryTradeScoreJoinExchangeRule(tradeId);
		if(IDataUtil.isNotEmpty(set)){
			for(int k=0;k<set.size();k++){
				String ruleId=set.getData(k).getString("RULE_ID","");
				String goodsNum=set.getData(k).getString("ACTION_COUNT","");
				String goodsName=set.getData(k).getString("GOODS_NAME","");
				String scoreChange=set.getData(k).getString("SCORE_CHANGED",""); 
				//要求必须是指定的礼品才执行插表及发短信。
				IDataset ruleSet= CommparaInfoQry.getCommPkInfo("CSM", "1122", ruleId, "0898"); 
				if(ruleSet!=null && ruleSet.size()>0){
					scoreChange=scoreChange.substring(scoreChange.indexOf("-")+1);
					String score=Integer.toString(Integer.parseInt(scoreChange)/Integer.parseInt(goodsNum)); 
					String endTime=SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 720);
					endTime=endTime.substring(0,endTime.lastIndexOf("-")+3);
					String remark="";
					String quancode="";
					GoodsApplyBean bean= BeanManager.createBean(GoodsApplyBean.class);
					for(int j=0;j<Integer.parseInt(goodsNum);j++){
						//生成短信验证码 4位数字 20160622 避免重复数据
						String verifyCode = RandomStringUtils.randomNumeric(4);
						while(quancode.indexOf(verifyCode)>-1){
							verifyCode = RandomStringUtils.randomNumeric(4);
						}
						if("".equals(quancode)){
							quancode=verifyCode;
						}else{
							quancode=quancode+","+verifyCode;
						}
						
						/*
						 * 2、插短信
						 * 下发4位验证码，要根据
						 * 您已成功使用$X积分兑换$Name礼品$QuanNum份，券号：$QuanCode，有效期至$EndTime。凭券到各营业网点领取。
						 * */
				    	String content="您已成功使用"+score+"积分兑换"+goodsName+"礼品1份，券号："+verifyCode+"，有效期至"+endTime+"。凭券到各营业网点领取。";
						IData smsData = new DataMap();
						smsData.put("SERIAL_NUMBER",serialNumber);
						smsData.put("USER_ID",userId);
						String sysTime=SysDateMgr.getSysTime();
						sysTime=sysTime.substring(sysTime.indexOf(":")-2); 				 
						smsData.put("NOTICE_CONTENT",content);
						smsData.put("STAFF_ID",CSBizBean.getVisit().getStaffId());
						smsData.put("DEPART_ID",CSBizBean.getVisit().getDepartId());
						smsData.put("REMARK","积分礼品验证码短信");
						smsData.put("BRAND_CODE","");
						smsData.put("FORCE_START_TIME","");
						smsData.put("FORCE_END_TIME","");
						bean.smsSent(smsData);
					}
					
					
					/*
					 * 1、插表TL_B_USER_SCORE_GOODS
					 * */
					IData insData=new DataMap();
					insData.put("USER_ID",userId);
			    	insData.put("RULE_ID",ruleId);
			    	insData.put("RULE_NAME",goodsName);
			    	insData.put("GOODS_NUM",goodsNum);
			    	insData.put("REMAIN_NUM",goodsNum); 
			    	insData.put("SCORE",score);
			    	insData.put("QUANCODE",quancode); 
			    	insData.put("SERIAL_NUMBER",serialNumber);
			    	insData.put("STATE","0");//默认为0未领取 
			    	insData.put("EXCHANGE_STAFF_ID",exchangeStaffId);
			    	insData.put("EXCHANGE_DEPART_ID",exchangeDepartId);
			    	insData.put("TRADE_ID",tradeId);
			    	insData.put("REMARK",remark);
			    	bean.insertGoodsTab(insData);
					
				}
			}
		}
	}
}