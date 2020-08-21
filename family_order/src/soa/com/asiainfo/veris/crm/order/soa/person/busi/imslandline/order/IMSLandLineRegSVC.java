package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.requestdata.IMSLandLineRequestData;

public class IMSLandLineRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "6800");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "6800");
    }
	@Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {
		IMSLandLineRequestData mergeWideUserCreateRD = (IMSLandLineRequestData) btd.getRD();
    	
    	String serialNumber = mergeWideUserCreateRD.getNormalSerialNumber();
    	String moProductId = mergeWideUserCreateRD.getMoProductId();
    	String moPackageId = mergeWideUserCreateRD.getMoPackageId();
    	String acceptTradeId = mergeWideUserCreateRD.getTradeId();
    	String moFee = mergeWideUserCreateRD.getMoFee();
        if (StringUtils.isNotBlank(moProductId) && StringUtils.isNotBlank(moPackageId))
        {
        	IData saleactiveData = new DataMap();
        	saleactiveData.put("SERIAL_NUMBER", serialNumber);
        	saleactiveData.put("PRODUCT_ID", moProductId);
        	saleactiveData.put("PACKAGE_ID", moPackageId);
        	saleactiveData.put("ACCEPT_TRADE_ID", acceptTradeId);
        	saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
        	IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCode4to6("CSM", "178", "6800", moProductId, moPackageId, "YX04" ,"0898");
			if(IDataUtil.isNotEmpty(commParaInfo6600)){
				saleactiveData.put("SALEGOODS_IMEI", mergeWideUserCreateRD.getResId());
			}
        	IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);

        	if(IDataUtil.isNotEmpty(result))
        	{
        		String tradeId = result.getData(0).getString("TRADE_ID","");
            	btd.getMainTradeData().setRsrvStr5(tradeId);
            	btd.getMainTradeData().setRsrvStr8(moFee);
        	}
        }
        
        
        String fixNumber=input.getString("WIDE_SERIAL_NUMBER");
        IDataset dataSet = ResCall.getMphonecodeInfo(fixNumber);
        if (IDataUtil.isNotEmpty(dataSet)){
        	IData mphonecodeInfo = dataSet.first();
        	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
        	if (StringUtils.equals("1", beautifulTag)&& input.getString("BIND_SALE_TAG", "").equals("1")){// 吉祥号码，且勾选了绑定的套餐。){
        		String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");
        		if(StringUtils.isBlank(productId)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+fixNumber+"需绑定的营销活动产品编码为空，请检查资源侧配置！");
        		}
        		String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
        		if(StringUtils.isBlank(packageId)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取吉祥号码"+fixNumber+"需绑定的营销包编码为空，请检查资源侧配置！");
        		}
        		btd.getRD().getUca().setAcctDay("1");
        		btd.getRD().getUca().setFirstDate("");
        		btd.getRD().getUca().setNextAcctDay("");
        		btd.getRD().getUca().setNextFirstDate("");
        	    IData saleactiveData = new DataMap();
                saleactiveData.put("SERIAL_NUMBER",fixNumber);
                saleactiveData.put("PRODUCT_ID",productId);
                saleactiveData.put("PACKAGE_ID", packageId);
                CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        	}
        	
        }
	}
}
