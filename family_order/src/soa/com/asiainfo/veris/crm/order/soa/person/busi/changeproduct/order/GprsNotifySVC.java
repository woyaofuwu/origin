package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order;

import org.apache.log4j.Logger;

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

public class GprsNotifySVC extends CSBizService{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.GprsNotifySVC.class);
	
	public IData getSpecTradeInfo(IData data) throws Exception
    {
        IData result = new DataMap();

        String custId = data.getString("CUST_ID");
        String userId = data.getString("USER_ID");
        IDataset custVip = CustVipInfoQry.getCustVipInfoByCustId(custId, "0");
        // 非大客户 和 CUST_MANAGER_ID为空的大客户 不调客服接,跳出子流程
        if (IDataUtil.isNotEmpty(custVip) 
        		&& StringUtils.isNotBlank(custVip.getData(0).getString("CUST_MANAGER_ID")))
        {
            IData vipInfo = custVip.getData(0);
            result.put("USECUST_NAME", vipInfo.getString("USECUST_NAME"));
            result.put("CUST_TYPE", vipInfo.getString("CUST_TYPE"));
            result.put("CUST_MANAGER_ID", vipInfo.getString("CUST_MANAGER_ID"));
            
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
		if(logger.isDebugEnabled()){
			logger.debug("GprsNotifySVC.dealExpire call, param:" + mainTrade.toString());
		}
		
        IDataset resultList = new DatasetList();
        IData callResult = new DataMap();
		
        String msg = mainTrade.getString("DEAL_COND");
        if("".equals(msg)){
	        callResult.put("X_RESULTINFO", "deal_code为空");
			resultList.add(callResult);
            return resultList;
        }
        String [] sbs = msg.split(",");
        if(sbs.length != 12){
	        callResult.put("X_RESULTINFO", "deal_code格式错误");
			resultList.add(callResult);
            return resultList;        	
        }
        
        int idx = 0;
        String userId = sbs[idx++];	//提醒用户的user_id
        String WorkId = sbs[idx++];	//工单标识
        String NoteCode = sbs[idx++];	//提醒编码
        String NoteMethod = sbs[idx++];	//提醒策略编码
        String alert_valueType = sbs[idx++];	//阀值类型
        String alert_value = sbs[idx++];	//阀值
        String available = sbs[idx++]; //可使用量
        String used = sbs[idx++];	//已使用量
        String remain = sbs[idx++];	//剩余量
        String type = sbs[idx++];	//值的类型me
        String NoteRule = sbs[idx++];	//提醒规则名称
        String WorkCreateTime = sbs[idx++];	//提醒工单的创建时间
	
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);	// --获取三户资料
        String creditClass = uca.getUserCreditClass();
        int cc = Integer.parseInt(creditClass);
		if ( cc < 4 ){
			callResult.put("X_RESULTINFO", "非vip客户，客户星级：" + creditClass);
			resultList.add(callResult);
            return resultList; 
		}
		
		String serialNumber = uca.getSerialNumber();
		String custId = uca.getCustomer().getCustId();
        String custName = uca.getCustomer().getCustName();
        String brandCode = uca.getBrandCode();
        
		mainTrade.put("CUST_ID", custId);
		mainTrade.put("CUST_NAME", custName);
		mainTrade.put("BRAND_CODE", brandCode);
		 
        IData specTradeInfo = getSpecTradeInfo(mainTrade);
        
        //callResult.put("CRM_TRADE_ID", tradeId);
        //callResult.put("CRM_TRADE_TYPE_CODE", tradeTypeCode);
        callResult.put("USER_ID", userId);
        callResult.put("CUST_ID", custId);
        callResult.put("CUST_NAME", custName);
        callResult.put("BRAND_CODE", brandCode);
        if (IDataUtil.isEmpty(specTradeInfo))
        {
	        callResult.put("X_RESULTINFO", "该用户没有客户经理资料！");
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
        String rsrvStr7 = NoteRule;
        
        // ITF_CRM_GPRSThresholdJob即将达到gprs流量
        resultList = SccCall.createGPRSThresholdJob(serialNumber, userId, custId, custName, 
        		custType, eparchyCode, brandCode, useCustName, custManagerId, remindTag, 
        		discntCode, discntName, rsrvStr7, available, used, remain);
     
		if(logger.isDebugEnabled()){
			logger.debug("GprsNotifySVC.dealExpire return, result:" + resultList.toString());
		}
		return resultList;
    }

}
