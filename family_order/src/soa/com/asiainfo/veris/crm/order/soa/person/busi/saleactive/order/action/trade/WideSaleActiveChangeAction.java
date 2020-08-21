package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class WideSaleActiveChangeAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
		SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
		String preType = req.getPreType();
		//System.out.println("---------------WideSaleActiveChangeAction--------------"+preType);
		if (preType.equals(BofConst.PRE_TYPE_CHECK))
		{
			return;
		}
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();
        String userId=btd.getMainTradeData().getUserId();
        String serialNum=btd.getRD().getUca().getSerialNumber();
        if("240".equals(tradeTypeCode))
    	{
        	List<SaleActiveTradeData> saleActiveTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        	for (int i = 0, size = saleActiveTradeDatas.size(); i < size; i++)
            {
                SaleActiveTradeData saleActiveTradeData = saleActiveTradeDatas.get(i);
                String productId=saleActiveTradeData.getProductId();
                String modifyTag=saleActiveTradeData.getModifyTag();
                if(("69908001".equals(productId) || "67220428".equals(productId)) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
                	/**
                	 * 查看是否存在包年套餐
                	 * 如果存在，则处理：
                	 * 1、获取包年套餐的剩余金额，退还至宽带包年活动变更预存回退存折9022 
                	 */
                	IDataset yearActives=SaleActiveInfoQry.getUserSaleActiveInfoInUse(userId, "67220428");
                	if(yearActives!=null && yearActives.size()>0){
                		
                		//转金额，要全部转，不能留一个月，否则会被沉淀
                		IData inparam=new DataMap();
                        //String everyMonFee=changeInfo.getString("ACTIVE_EVERY_MON_FEE");//每月费用
                    	inparam.put("SERIAL_NUMBER_1",serialNum);
                    	inparam.put("SERIAL_NUMBER_2",serialNum);
                    	inparam.put("DEPOSIT_CODE_1","9021");
                    	inparam.put("DEPOSIT_CODE_2","9022"); 
                    	inparam.put("REMARK","营销活动-宽带1+办理返还包年存折金额。");
                    	inparam.put("USER_ID_IN",userId);
                    	inparam.put("USER_ID_OUT",userId); 
                    	inparam.put("DEDUCT_TYPE_CODE","1"); 
                    	//inparam.put("ACTIVE_EVERY_MON_FEE",everyMonFee); 
                    	transRemainFeeAtoB(inparam);
                	}
                }
                
                if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                	if("JZF".equals(req.getRsrvStr10()) && StringUtils.isNotBlank(req.getRsrvStr9()) )
                	{
                		int balance9021 = 0,balance9023 = 0;
                    	int backFee = Integer.parseInt(req.getRsrvStr9());
                    	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNum);
                        if(IDataUtil.isNotEmpty(allUserMoney)){
                        	for(int z=0;z<allUserMoney.size();z++){
                    			String balance1 = allUserMoney.getData(z).getString("DEPOSIT_BALANCE","0");
                                int balance2 = Integer.parseInt(balance1);
                        		if("9021".equals(allUserMoney.getData(z).getString("DEPOSIT_CODE"))){
                                    balance9021 = balance9021 + balance2;
                        		}
                        	}
                        	String strRemark = backFee +"," + balance9021;
                        	if(backFee<balance9021){ 
                        		if(backFee>0){
                        			IData param = new DataMap();
                        			param.put("SERIAL_NUMBER_1", serialNum);
                        	    	param.put("SERIAL_NUMBER_2", serialNum);
                        	    	param.put("REMARK",  strRemark);
                                	param.put("DEPOSIT_CODE_1", "9021");
                                	param.put("DEPOSIT_CODE_2", "9022");
                                	param.put("FEE", backFee);
                                	try{
                                		IData backEnds = AcctCall.depositeToPhoneMoney(param);
                                		if(IDataUtil.isNotEmpty(backEnds)&&("0".equals(backEnds.getString("RESULT_CODE",""))||"0".equals(backEnds.getString("X_RESULTCODE","")))){
                                		// 成功！ 处理other表
                                		}else{
                                			CSAppException.appError("61312", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折)错误:"+"入参：【"+param.toString() + "】" + backEnds.getString("X_RESULTINFO"));
                                		}
                                	} catch (Exception e){
                                		CSAppException.appError("61313", "调用接口AM_CRM_TransFee转存(宽带包年活动专项款存折)错误:"+"入参：【"+param.toString() + "】" + e.getStackTrace());
                            		}
                            	}
                        	}
                        }
                	}
                }
            }
    	}
	}
	
	
	/**
     * 扣转新用户的光猫押金。现金-存折
     * SERIAL_NUMBER_1	              转出号码
		SERIAL_NUMBER_2	              转入号码
		DEPOSIT_CODE_1	              转出帐本编码
		DEPOSIT_CODE_2	              转入帐本编码
		FEE	 	                转账金额
		REMARK	 	              备注
		USER_ID_IN	               转入用户ID
		USER_ID_OUT	            转出用户ID
     * */
    private void transRemainFeeAtoB(IData inparam) throws Exception{
    	IData rtnData=new DataMap();
    	String DEPOSIT_BALANCE9020="0";//存折余额9020存折
    	String DEPOSIT_BALANCE9021="0";//存折余额9021存折
    	String DEPOSIT_BALANCE9023="0";//存折余额9023存折
    	//String DEPOSIT_BALANCE9024="0";//存折余额9024存折
		//3、获取默认账户  （acct_id)		
    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(inparam.getString("SERIAL_NUMBER_1"));
    	String acctId=accts.getData(0).getString("ACCT_ID");
    	IData param = new DataMap();
    	param.put("ACCT_ID", acctId); 
    	/**调用账务查询接口*/
    	IDataset checkCash= AcctCall.queryAcctDeposit(param); 
    	 
    	if(checkCash!=null && checkCash.size()>0){
    		for(int j=0;j<checkCash.size();j++){
    			IData acctInfo=checkCash.getData(j);
	    		String DEPOSIT_CODE=acctInfo.getString("DEPOSIT_CODE");//存折编码
	    		
	    		if("9020".equals(DEPOSIT_CODE)){ 
	    			DEPOSIT_BALANCE9020=""+(Integer.parseInt(DEPOSIT_BALANCE9020)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		
	    		if("9021".equals(DEPOSIT_CODE)){
	    			DEPOSIT_BALANCE9021=""+(Integer.parseInt(DEPOSIT_BALANCE9021)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		
	    		if("9023".equals(DEPOSIT_CODE)){ 
	    	    	DEPOSIT_BALANCE9023=""+(Integer.parseInt(DEPOSIT_BALANCE9023)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
	    		}
	    		
//	    		if("9024".equals(DEPOSIT_CODE)){ 
//	    			DEPOSIT_BALANCE9024=""+(Integer.parseInt(DEPOSIT_BALANCE9024)+Integer.parseInt(acctInfo.getString("DEPOSIT_BALANCE")));//存折余额
//	    		}
	    		
    		}
    	}
    	//转9023存折
    	if(Integer.parseInt(DEPOSIT_BALANCE9023)>0){
    		inparam.put("DEPOSIT_CODE_1","9023");
	    	inparam.put("DEPOSIT_CODE_2","9022"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9023);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	}
    	//转9021存折
    	if(Integer.parseInt(DEPOSIT_BALANCE9021)>0){
    		inparam.put("DEPOSIT_CODE_1","9021");
	    	inparam.put("DEPOSIT_CODE_2","9022"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9021);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	} 
    	
    	//转9020存折
/*    	if(Integer.parseInt(DEPOSIT_BALANCE9020)>0){
    		inparam.put("DEPOSIT_CODE_1","9020");
	    	inparam.put("DEPOSIT_CODE_2","9022"); 
	    	inparam.put("FEE",DEPOSIT_BALANCE9020);//A用户活动余额
	    	AcctCall.transRemainFeeAtoB(inparam);
    	} */
    	
//    	//转9024存折
//    	if(Integer.parseInt(DEPOSIT_BALANCE9024)>0){
//    		inparam.put("DEPOSIT_CODE_1","9024");
//	    	inparam.put("DEPOSIT_CODE_2","9022"); 
//	    	inparam.put("FEE",DEPOSIT_BALANCE9024);//A用户活动余额
//	    	AcctCall.transRemainFeeAtoB(inparam);
//    	} 
    }
}
