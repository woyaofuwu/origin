package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class DiscntNotify4gSVC extends CSBizService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IData getSpecTradeInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        String custId = data.getString("CUST_ID");
        String userId = data.getString("USER_ID");
        IDataset custVip = CustVipInfoQry.getCustVipInfoByCustId(custId, "0");
        // 非大客户 和 CUST_MANAGER_ID为空的大客户 不调客服接,跳出子流程
        if (IDataUtil.isNotEmpty(custVip) && StringUtils.isNotBlank(custVip.getData(0).getString("CUST_MANAGER_ID")))
        {
            IData vipInfo = custVip.getData(0);
            result.put("USECUST_NAME", vipInfo.getString("USECUST_NAME"));
            result.put("CUST_TYPE", vipInfo.getString("CUST_TYPE"));
            result.put("CUST_MANAGER_ID", vipInfo.getString("CUST_MANAGER_ID"));
            String strHour = SysDateMgr.getSysDate("HH");
            String strDay = SysDateMgr.getSysDate("DD");
            if( "8".equals(strHour) && "3".equals(strDay) ){
            	result.put("RSRV_STR7", "次月风险提醒");
            }else{
            	result.put("RSRV_STR7", "3日风险提醒");
            }
            
            IDataset userSvc = UserSvcInfoQry.getSvcUserId(userId, "650");
            if (IDataUtil.isNotEmpty(userSvc))
            {
                result.put("REMIND_TAG", 1);
            }
            else
            {
                result.put("REMIND_TAG", 0);
            }

            IDataset userDiscnt = UserDiscntInfoQry.getUserSpecDiscnt(userId, "5");
            if (IDataUtil.isNotEmpty(userDiscnt))
            {
                result.put("DISCNT_CODE", userDiscnt.getData(0).getString("DISCNT_CODE"));
                result.put("DISCNT_NAME", userDiscnt.getData(0).getString("DISCNT_NAME"));
            }
            else
            {
                result.put("DISCNT_CODE", 0);
                result.put("DISCNT_NAME", "【客户没有订购GPRS主套餐！】");
            }
        }
        return result;
    }

	public IDataset dealExpire(IData mainTrade) throws Exception
    {
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);	// --获取三户资料
		String custId = uca.getCustomer().getCustId();
        String custName = uca.getCustomer().getCustName();
        String brandCode = uca.getBrandCode();
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        
		mainTrade.put("CUST_ID", custId);
		mainTrade.put("CUST_NAME", custName);
		mainTrade.put("BRAND_CODE", brandCode);
		
		/*String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String tradeId = mainTrade.getString("TRADE_ID");*/
        String userId = mainTrade.getString("USER_ID");
        IData specTradeInfo = getSpecTradeInfo(mainTrade);
        
        IDataset resultList = new DatasetList();
        IData callResult = new DataMap();
        //callResult.put("CRM_TRADE_ID", tradeId);
        //callResult.put("CRM_TRADE_TYPE_CODE", tradeTypeCode);
        callResult.put("USER_ID", userId);
        callResult.put("CUST_ID", custId);
        callResult.put("CUST_NAME", custName);
        callResult.put("BRAND_CODE", brandCode);
        if (IDataUtil.isEmpty(specTradeInfo))
        {
	        callResult.put("X_RESULTINFO", "该用户，非大客户！");
	        //callResult.put("CRM_TRADE_ID", tradeId);
			resultList.add(callResult);
            return resultList;
        }
		
        String custType = specTradeInfo.getString("CUST_TYPE");
        String useCustName = specTradeInfo.getString("USECUST_NAME");
        String custManagerId = specTradeInfo.getString("CUST_MANAGER_ID");
        String remindTag = specTradeInfo.getString("REMIND_TAG");
        String discntCode = specTradeInfo.getString("DISCNT_CODE");
        String discntName = specTradeInfo.getString("DISCNT_NAME");
        String rsrvStr7 = specTradeInfo.getString("RSRV_STR7");
        // ITF_CRM_GPRSThresholdJob即将达到gprs流量
        resultList = SccCall.createGPRSThresholdJob(serialNumber, userId, custId, custName, custType, eparchyCode, brandCode, useCustName, custManagerId, remindTag, discntCode, discntName, rsrvStr7);
     
		return resultList;
    }

}
