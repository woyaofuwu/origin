
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class CreateTDPersonUserRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "3820");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "3820");
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (!input.getString("SALEACTIVE_DATA", "").equals(""))
        {
            IData saleactiveData = new DataMap(input.getString("SALEACTIVE_DATA"));
            saleactiveData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            saleactiveData.put("X_TRADE_FEESUB", input.getString("X_TRADE_FEESUB"));
            saleactiveData.put("B_REOPEN_TAG", input.getString("B_REOPEN_TAG"));
            CSAppCall.call("SS.SaleActiveSVC.tradeReg", saleactiveData);
        }
        
        String serialNumber=input.getString("SERIAL_NUMBER");
        IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
        if (IDataUtil.isNotEmpty(dataSet)){
        	IData mphonecodeInfo = dataSet.first();
        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
        	if (StringUtils.equals("1", beautifulTag)){
        		String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
        		if(StringUtils.isBlank(productId)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
        		}
        		String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
        		if(StringUtils.isBlank(packageId)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+serialNumber+"需绑定的营销包编码为空，请检查资源侧配置！");
        		}
        		btd.getRD().getUca().setFirstDate("");
        		btd.getRD().getUca().setNextAcctDay("");
        		btd.getRD().getUca().setNextFirstDate("");
        	    IData saleactiveData = new DataMap();
                saleactiveData.put("SERIAL_NUMBER",serialNumber);
                saleactiveData.put("PRODUCT_ID",productId);
                saleactiveData.put("PACKAGE_ID", packageId);
                CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        	}
        	
        }
    }
}
