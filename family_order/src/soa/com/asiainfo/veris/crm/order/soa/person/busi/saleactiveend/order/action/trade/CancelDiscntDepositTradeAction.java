
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;

public class CancelDiscntDepositTradeAction implements ITradeAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();
        UcaData uca = saleactiveEndReqData.getUca();
        
        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleactiveEndReqData.getCallType()))
            return;

        //REQ202003180001“共同战疫宽带助力”活动开发需求start
        //配置的活动沉淀走AM_CRM_BackFee接口(SaleActiveEndDepositTradeAction)
        String productId = saleactiveEndReqData.getProductId();
        String packageId = saleactiveEndReqData.getPackageId();
        IDataset com3228 = CommparaInfoQry.getCommparaByCode1("CSM", "3228", productId, packageId, null);
        if(IDataUtil.isEmpty(com3228)){
            com3228 = CommparaInfoQry.getCommparaByCode1("CSM", "3228", productId, "-1", null);
        }
        if(IDataUtil.isNotEmpty(com3228)){
            return;
        }
        //REQ202003180001“共同战疫宽带助力”活动开发需求end


        String relationTradeId = saleactiveEndReqData.getRelationTradeId();

        String actionCode = SaleActiveUtil.getEnableActiveActionCode(saleactiveEndReqData.getUca(), relationTradeId);
        String forwordFlag = "";
        if (StringUtils.isBlank(actionCode)) 
        {
            forwordFlag = "1";
            boolean thisActiveIsBackActive = SaleActiveUtil.isBackActive(saleactiveEndReqData.getProductId(), uca.getUserEparchyCode());
            if(!thisActiveIsBackActive){
               return;
            }
        }

        String userId = saleactiveEndReqData.getUca().getUserId();
        int intervalMoth = saleactiveEndReqData.getIntervalMonth();
        String eparchyCode = saleactiveEndReqData.getUca().getUserEparchyCode();
        
        //如果已经返还结束，则不需要再调账务接口
        if(intervalMoth<0){
        	return;
        }
        try{
        	
        	IData saleinfo =AcctCall.cancelDiscntDeposit(userId, relationTradeId, intervalMoth, eparchyCode, forwordFlag).getData(0);
            if(IDataUtil.isNotEmpty(saleinfo))
            {
//    	        UcaData uca = saleactiveEndReqData.getUca();
    	        SaleActiveTradeData userSaleActiveTradeData = uca.getUserSaleActiveByRelaTradeId(saleactiveEndReqData.getRelationTradeId());
    	        userSaleActiveTradeData.setRsrvStr4(saleinfo.getString("DEPOSIT_MONEY"));//预存金额
    	        userSaleActiveTradeData.setRsrvStr5(saleinfo.getString("PRESENT_MONEY"));	 //赠送金额       
            }
        	       	
        }catch (Exception e) {
        	if(!e.toString().contains("未返销的活动记录"))
        	{
        		System.out.println(e);
            	throw e;
        	}
        	
		}
        
    }
}



