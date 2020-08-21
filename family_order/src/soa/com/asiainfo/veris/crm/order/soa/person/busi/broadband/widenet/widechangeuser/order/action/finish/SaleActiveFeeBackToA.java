
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry; 

/**
 * 宽带过户
 * 完工后将宽带营销活动一个月的费用转移到A用户，用于保证A已经预扣的宽带费用下个月初能正常实扣
 * 
 *  候鸟只有9015存折扣款；包年和学期包先扣9023，如果不够再扣9021
 */
public class SaleActiveFeeBackToA implements ITradeFinishAction
{
	private static final Logger log = Logger.getLogger(SaleActiveFeeBackToA.class);
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
        String orderId = mainTrade.getString("ORDER_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        log.debug("------------***cxy***--------orderId="+orderId);
        if("640".equals(tradeTypeCode)){ 
        	String kd_serialNumber = mainTrade.getString("SERIAL_NUMBER");
            String old_serialNum="";
            String new_serialNum="";
            String old_userId="";
            String new_userId=""; 
            String acctID_B="";
            String productId="";
    		String packageId="";
    		boolean ifchangeSaleActive=false;//有转营销活动才转账
            String ACTIVE_EVERY_MON_FEE = mainTrade.getString("RSRV_STR2");//营销活动每月金额，用于返还A账本
            log.debug("------------***cxy***--------ACTIVE_EVERY_MON_FEE="+ACTIVE_EVERY_MON_FEE);
            if(ACTIVE_EVERY_MON_FEE!=null && !"".equals(ACTIVE_EVERY_MON_FEE)){
			if(orderId!=null&&!"".equals(orderId)){
				IDataset tradeInfos = TradeInfoQry.getHisMainTradeByOrderId(orderId, "0",CSBizBean.getUserEparchyCode());
	        	if(IDataUtil.isNotEmpty(tradeInfos)){
	        		for(int j=0;j<tradeInfos.size();j++){
	        			if("237".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE"))){ 
	        				old_serialNum=tradeInfos.getData(j).getString("SERIAL_NUMBER","");
	        				old_userId =tradeInfos.getData(j).getString("USER_ID","");   
	        				ifchangeSaleActive=true;
	        				log.debug("------------***cxy***--------old_serialNum="+old_serialNum);
	        			}
	        			//新号码
	        			if("240".equals(tradeInfos.getData(j).getString("TRADE_TYPE_CODE"))){
	        				acctID_B=tradeInfos.getData(j).getString("ACCT_ID","");
	        				productId=tradeInfos.getData(j).getString("RSRV_STR1","");
	        				packageId=tradeInfos.getData(j).getString("RSRV_STR2","");
	        				new_serialNum=tradeInfos.getData(j).getString("SERIAL_NUMBER","");
	        				new_userId =tradeInfos.getData(j).getString("USER_ID",""); 
	        				ifchangeSaleActive=true;
	        				log.debug("------------***cxy***--------new_serialNum="+new_serialNum);
	        			}
	        		}
	        	}
			}
			//存在营销活动的工单才进行转账
			if(ifchangeSaleActive){
				String DEPOSIT_BALANCE9021="0";//存折余额9021存折
		    	String DEPOSIT_BALANCE9023="0";//存折余额9023存折
		    	String DEPOSIT_BALANCE9015="0";//候鸟套餐的存折
				 	 
		    	IData param = new DataMap();
		    	param.put("ACCT_ID", acctID_B); //B用户
		    	/**调用账务查询接口 查询B用户的账本信息*/
		    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
		    	 
		    	if(checkCash!=null && checkCash.size()>0){
		    		for(int j=0;j<checkCash.size();j++){
		    			IData acctInfo=checkCash.getData(j);
			    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
			    		
			    		if("9021".equals(DEPOSIT_CODE)){
			    			DEPOSIT_BALANCE9021=""+(Integer.parseInt(DEPOSIT_BALANCE9021)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
			    		}
			    		
			    		if("9023".equals(DEPOSIT_CODE)){ 
			    	    	DEPOSIT_BALANCE9023=""+(Integer.parseInt(DEPOSIT_BALANCE9023)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
			    		}
			    		if("9015".equals(DEPOSIT_CODE)){ 
			    			DEPOSIT_BALANCE9015=""+(Integer.parseInt(DEPOSIT_BALANCE9015)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
			    		} 
		    		}
		    	}
		    	IData inparam=new DataMap();
		         //String everyMonFee=changeInfo.getString("ACTIVE_EVERY_MON_FEE");//每月费用
		     	inparam.put("SERIAL_NUMBER_1",new_serialNum);
		     	inparam.put("SERIAL_NUMBER_2",old_serialNum); 
		     	inparam.put("REMARK","宽带过户-B活动转入A一个月金额");
		     	inparam.put("USER_ID_OUT",new_userId); 
		     	inparam.put("USER_ID_IN",old_userId);
		     	inparam.put("DEDUCT_TYPE_CODE","1");
		     	log.debug("------------***cxy***--------new_serialNum="+new_serialNum);
		     	if("67220428".equals(productId) || "67220429".equals(productId)){//包年、学期包 
		     		//转9023存折
			    	if(Integer.parseInt(DEPOSIT_BALANCE9023)>Integer.parseInt(ACTIVE_EVERY_MON_FEE)){
			    		inparam.put("DEPOSIT_CODE_1","9023");
				    	inparam.put("DEPOSIT_CODE_2","9023"); 
				    	inparam.put("FEE",ACTIVE_EVERY_MON_FEE);//A用户活动余额
				    	IData callBack=AcctCall.transRemainFeeAtoB(inparam);
				    	log.debug("------------***cxy***--------callBack9023="+callBack);
			    	}else{
			     		//转9021存折
				    	if(Integer.parseInt(DEPOSIT_BALANCE9021)>Integer.parseInt(ACTIVE_EVERY_MON_FEE)){
				    		inparam.put("DEPOSIT_CODE_1","9021");
					    	inparam.put("DEPOSIT_CODE_2","9021"); 
					    	inparam.put("FEE",ACTIVE_EVERY_MON_FEE);//A用户活动余额
					    	IData callBack=AcctCall.transRemainFeeAtoB(inparam);
					    	log.debug("------------***cxy***--------callBack9021="+callBack);
				    	}else{
				    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户【"+new_serialNum+"】的9023、9021存折余额不够转给A用户用于销账。转移金额需要【"+ACTIVE_EVERY_MON_FEE+"】，存折余额9023【"+DEPOSIT_BALANCE9023+"】9021【"+DEPOSIT_BALANCE9021+"】");
				    	} 
			    	}
		     	}
		    	
		    	//转9015存折 候鸟套餐的存折
		    	if("69908016".equals(productId)){
		    		if(Integer.parseInt(DEPOSIT_BALANCE9015)>0){
			    		inparam.put("DEPOSIT_CODE_1","9015");
				    	inparam.put("DEPOSIT_CODE_2","9015"); 
				    	inparam.put("FEE",ACTIVE_EVERY_MON_FEE);//A用户活动余额
				    	IData callBack=AcctCall.transRemainFeeAtoB(inparam);
				    	log.debug("------------***cxy***--------callBack9015="+callBack);
		    		}else{
			    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户【"+new_serialNum+"】的9015存折余额不够转给A用户用于销账。转移金额需要【"+ACTIVE_EVERY_MON_FEE+"】，存折余额9015【"+DEPOSIT_BALANCE9015+"】");
			    	} 
		    	}
			}
        }
	    }	 
    } 
}
