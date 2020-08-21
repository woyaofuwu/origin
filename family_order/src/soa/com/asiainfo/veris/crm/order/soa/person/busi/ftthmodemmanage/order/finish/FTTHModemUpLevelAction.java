package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * [SPAM]宽带提速活动
 * 光猫升级完工把调测费从中间存折转入专项存折；有光猫押金并未满三年进行返还
 * @CREATED by lizj@2020-03-10
 */
public class FTTHModemUpLevelAction implements ITradeFinishAction{
	
	protected static Logger log = Logger.getLogger(FTTHModemUpLevelAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String userId = mainTrade.getString("USER_ID");
		String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId,"FTTH",userId);
        for(int i=0;i<tradeOtherInfos.size();i++){
        	IData tradeOtherInfo = tradeOtherInfos.getData(i);
        	String modify = tradeOtherInfo.getString("MODIFY_TAG");
        	String instId = tradeOtherInfos.getData(i).getString("INST_ID");
        	String moderm_id = tradeOtherInfos.getData(i).getString("RSRV_STR1");
        	String deposit = tradeOtherInfos.getData(i).getString("RSRV_STR2","0");//押金

        	if(BofConst.MODIFY_TAG_DEL.equals(modify)){
    	        if(Integer.parseInt(deposit) > 0){
    	        	String modemStartDate = tradeOtherInfos.getData(i).getString("START_DATE",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    	        	String sysdate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
    	        	String newmodemStartDate=SysDateMgr.addDays(modemStartDate, 1095);
                    if(newmodemStartDate.compareTo(sysdate)>0){//用户光猫小于三年,押金返回
                    	IDataset allDeposit = AcctCall.queryAccountDepositBySn(serialNumber);
        				int deposit9002 = 0;
        				if (IDataUtil.isNotEmpty(allDeposit)) {
        					for (int j = 0; j < allDeposit.size(); j++) {
        						// System.out.println(allDeposit.getData(i).getString("DEPOSIT_CODE"));
        						//取出类型为9002的押金
        						if ("9002".equals(allDeposit.getData(j).getString("DEPOSIT_CODE"))) {
        							deposit9002 = deposit9002 + Integer.parseInt(allDeposit.getData(j).getString("DEPOSIT_BALANCE", "0"));
        						}

        					}
        				}
        				//如果有押金不为0，退还光猫时增加退押金功能
        				if(deposit9002>0){
        					//3、获取默认账户  （acct_id)
        			    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
        			    	String acctId=accts.getData(0).getString("ACCT_ID");
        					//调账务提供的接口将宽带光猫押金存折的钱转到现金存折； 
        		    		IData inparams=new DataMap();
        		    		inparams.put("SERIAL_NUMBER", serialNumber);
        		    		//inparams.put("OUTER_TRADE_ID", input.first().getString("RSRV_STR8",""));
        		    		//inparams.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
        		    		inparams.put("DEPOSIT_CODE_OUT", "9002");
        		    		inparams.put("DEPOSIT_CODE_IN", "0");
        		    		inparams.put("TRADE_FEE", deposit);
        		    		inparams.put("CHANNEL_ID", "15000");
        		    		inparams.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
        		    		inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        		    		inparams.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
        		    		inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());  
        		    		inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        		    		IData resultData = AcctCall.transFeeInADSL(inparams);
        		    		String result=resultData.getString("RESULT_CODE","");
        		              
    		                if("".equals(result) || !"0".equals(result))
    		                {
    		                  CSAppException.appError("71352", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + inparams + "错误:" + resultData.getString("RESULT_INFO"));
    		                }
        				}
                    }
    				
    			}
    	       
        	}
        }
        
        
        IDataset allDeposit = AcctCall.queryAccountDepositBySn(serialNumber);
		int deposit9440 = 0;
		if (DataSetUtils.isNotBlank(allDeposit)) {
			for (int j = 0; j < allDeposit.size(); j++) {
				// System.out.println(allDeposit.getData(i).getString("DEPOSIT_CODE"));
				//取出类型为9440的押金
				if ("9440".equals(allDeposit.getData(j).getString("DEPOSIT_CODE"))) {
					deposit9440 = deposit9440 + Integer.parseInt(allDeposit.getData(j).getString("DEPOSIT_BALANCE", "0"));
				}

			}
		}
		//避免二次转账
		if(deposit9440>0){
			IData params=new DataMap(); 
			params.put("SERIAL_NUMBER", serialNumber);
			params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	        params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        params.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
	        params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	        params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	        params.put("OUTER_TRADE_ID", SeqMgr.getTradeId());
	        params.put("DEPOSIT_CODE_OUT","9440");//中间存折
	        params.put("DEPOSIT_CODE_IN", "440");//正式存折
	        params.put("TRADE_FEE", "10000");
	        params.put("CHANNEL_ID", "15000");
	        if(StringUtils.isNotBlank(params.getString("TRADE_FEE"))){
	          	 //调用接口，将【中间存折】——>【现金】
	              IData resultData = AcctCall.transFeeInADSL(params);
	              String result=resultData.getString("RESULT_CODE","");
	              
	              if("".equals(result) || !"0".equals(result))
	              {
	                  CSAppException.appError("71352", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
	              }
	        }
		}
        	
        
        
        
	}

}
