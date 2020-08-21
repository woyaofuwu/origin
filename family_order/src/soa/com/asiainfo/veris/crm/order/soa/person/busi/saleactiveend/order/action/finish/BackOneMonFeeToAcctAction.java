
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class BackOneMonFeeToAcctAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	String serialNum=mainTrade.getString("SERIAL_NUMBER");
    	String userId=mainTrade.getString("USER_ID");
    	String acctId=mainTrade.getString("ACCT_ID");
    	String orderId=mainTrade.getString("ORDER_ID");
    	String tradeId=mainTrade.getString("TRADE_ID");
    	String prodId=mainTrade.getString("RSRV_STR1");//终止的包年活动
    	String packId=mainTrade.getString("RSRV_STR2");//终止的包年活动
    	if("67220428".equals(prodId)){
    		IData param=new DataMap();
    		param.put("ORDER_ID", orderId);
    		param.put("TRADE_ID", tradeId);
    		IDataset discntInfos=qryTradeDiscntInfo(param);//如果优惠还未生效的，首免期内，则不再转一个月回来
    		if(discntInfos!=null && discntInfos.size()>0){
    			IDataset tradeInfos=qryTradeInfo(param);//同一ORDER_ID存在另一笔240工单是宽带1+办理的
        		if(tradeInfos!=null && tradeInfos.size()>0){
        			String productId=tradeInfos.getData(0).getString("RSRV_STR1","");//新办理的宽带1+
        			//计算包年活动每月费用
                	int activeFee=0;
                	int monFee=0;//每月费用
                	IDataset feeInfos = WideNetUtil.getWideNetSaleAtiveTradeFee(prodId, packId);  
    		        for(int j = 0 ; j < feeInfos.size() ; j++)
    		        {
    		            IData feeData = feeInfos.getData(j); 
    		            String feeMode = feeData.getString("FEE_MODE");
    		            String fee = feeData.getString("FEE");
    		            
    		            if(fee != null && !"".equals(fee) && Integer.parseInt(fee) >0 &&  "2".equals(feeMode))
    		            { 
    		            	 
    		            	activeFee += Integer.parseInt(fee);
    		            } 
    		        }
    				if(activeFee>0){
    					if("67220428".equals(prodId)){//包年的是12个月
    						monFee=activeFee/12;
    						
    						//麻痹的，不能整除12
    						IData inparams=new DataMap();
    						inparams.put("USER_ID", userId);
    						inparams.put("PRODUCT_ID", prodId);
    						inparams.put("PACKAGE_ID", packId); 
    						
    						IDataset discnts=getDiscntInfo(inparams);
    						String paraCode8 = "";
    						String paraCode9 = "";
    						String paraCode10 = "";
    						if(IDataUtil.isNotEmpty(discnts))
    						{
    							paraCode8=discnts.getData(0).getString("PARA_CODE8","");
    							paraCode9=discnts.getData(0).getString("PARA_CODE9","");
    							paraCode10=discnts.getData(0).getString("PARA_CODE10","");
    							
    							if("1".equals(paraCode8))
    							{
    								IDataset ds = TradeSaleActive.getTradeSaleActive(tradeId, userId, "1");
    								if(IDataUtil.isNotEmpty(ds))
    								{
    									String startDate=ds.getData(0).getString("START_DATE","");
    									int currentUseMonths = 0;
    									String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
    	                                currentUseMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(firstDayOfNextMonth,"yyyy-MM-dd","yyyyMM"));
    	                                if(currentUseMonths<=11)
    	                                {
    	                                	monFee = Integer.parseInt(paraCode9);
    	                                }else
    	                                {
    	                                	monFee = Integer.parseInt(paraCode10);
    	                                }
    								}
    								
    							}
    						}
    						
    					}
    				}
    				String DEPOSIT_BALANCE9022="0";//存折余额9022存折 
    				 	 
    		    	IData params = new DataMap();
    		    	params.put("ACCT_ID", acctId); //B用户
    		    	/**调用账务查询接口 查询B用户的账本信息*/
    		    	IDataset checkCash= AcctCall.queryAcctDeposit(params); 
    		    	 
    		    	if(checkCash!=null && checkCash.size()>0){
    		    		for(int j=0;j<checkCash.size();j++){
    		    			IData acctInfo=checkCash.getData(j);
    			    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
    			    		
    			    		if("9022".equals(DEPOSIT_CODE)){
    			    			DEPOSIT_BALANCE9022=""+(Integer.parseInt(DEPOSIT_BALANCE9022)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
    			    		} 
    		    		}
    		    	}
    		    	if(Integer.parseInt(DEPOSIT_BALANCE9022)>=monFee){
    					IData inparam=new DataMap();
    			         //String everyMonFee=changeInfo.getString("ACTIVE_EVERY_MON_FEE");//每月费用
    			     	inparam.put("SERIAL_NUMBER_1",serialNum);
    			     	inparam.put("SERIAL_NUMBER_2",serialNum); 
    			     	inparam.put("REMARK","宽带包年活动转回一个月费用用于下月扣费");
    			     	inparam.put("USER_ID_OUT",userId); 
    			     	inparam.put("USER_ID_IN",userId);
    			     	inparam.put("DEPOSIT_CODE_1","9022");
    			    	inparam.put("DEPOSIT_CODE_2","9021"); 
    			    	inparam.put("FEE",""+monFee);//A用户活动余额
    			    	inparam.put("DEDUCT_TYPE_CODE","1");
    			    	AcctCall.transRemainFeeAtoB(inparam);
    		    	}
        		}
    		}    		
    	} 
    }
    
    /**
     * 根据ORDER_ID查询是否还有一笔办理宽带1+活动的工单
     * */
    public IDataset qryTradeInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select t.*  from tf_b_trade t ");
        parser.addSQL(" where t.order_id=:ORDER_ID ");
        parser.addSQL(" and (t.rsrv_str1='69908001' or t.rsrv_str1='67220428') ");
        parser.addSQL(" and t.trade_type_code='240' ");
        parser.addSQL(" and t.cancel_tag='0' ");  
        parser.addSQL(" union ");  
        parser.addSQL(" select t.*  from tf_bh_trade t ");
        parser.addSQL(" where t.order_id=:ORDER_ID ");
        parser.addSQL(" and (t.rsrv_str1='69908001' or t.rsrv_str1='67220428') ");
        parser.addSQL(" and t.trade_type_code='240' ");
        parser.addSQL(" and t.cancel_tag='0' ");  
        return Dao.qryByParse(parser,Route.getJourDb());  
    } 
    
    public IDataset qryTradeDiscntInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select  t.*  from tf_b_trade_discnt t ");
        parser.addSQL(" where t.trade_id=:TRADE_ID   ");
        parser.addSQL(" and sysdate > t.start_date  ");
        parser.addSQL(" and exists (select 1 from td_s_commpara r where r.param_attr = '7112' "); 
        parser.addSQL(" and r.para_code2 = '67220428' and r.param_code = t.discnt_code and r.end_date > sysdate) "); 
        return Dao.qryByParse(parser,Route.getJourDb());  
    } 
    
    public static IDataset getDiscntInfo(IData inparams) throws Exception{
		 SQLParser parser = new SQLParser(inparams); 
	     parser.addSQL(" select t.* from td_s_commpara t   ");
	     parser.addSQL(" where t.subsys_code='CSM'  ");
	     parser.addSQL(" AND T.PARAM_ATTR='7112'  ");
	     parser.addSQL(" AND T.PARA_CODE1=:PACKAGE_ID  ");
	     parser.addSQL(" AND T.PARA_CODE2=:PRODUCT_ID  ");
	     parser.addSQL(" AND SYSDATE < T.END_DATE  ");
	     parser.addSQL(" and T.PARA_CODE2 in (67220428,67220429,69908016,69908001,69908015) "); 
	     return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
}
