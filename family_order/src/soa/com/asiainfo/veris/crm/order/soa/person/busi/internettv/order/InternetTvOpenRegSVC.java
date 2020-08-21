package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;

public class InternetTvOpenRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "4800");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "4800");
    }
    
    /**
     * 通过调营销活动受理接口，办理一笔预受理的魔百和营销活动
     * */
	@Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {
    	InternetTvOpenRequestData mergeWideUserCreateRD = (InternetTvOpenRequestData) btd.getRD();
    	
    	String serialNumber = mergeWideUserCreateRD.getNormalSerialNumber();
    	String moProductId = mergeWideUserCreateRD.getMoProductId();
    	String moPackageId = mergeWideUserCreateRD.getMoPackageId();
    	String acceptTradeId = mergeWideUserCreateRD.getTradeId();
    	String moFee = mergeWideUserCreateRD.getMoFee();
        
        //选了魔百和营销活动
        if (StringUtils.isNotBlank(moProductId) && StringUtils.isNotBlank(moPackageId))
        {
        	IData saleactiveData = new DataMap();
        	saleactiveData.put("SERIAL_NUMBER", serialNumber);
        	saleactiveData.put("PRODUCT_ID", moProductId);
        	saleactiveData.put("PACKAGE_ID", moPackageId);
        	saleactiveData.put("ACCEPT_TRADE_ID", acceptTradeId);//用于SaleActiveTrade.java能记录该工单单号到book表，完工时调用DealSaleActiveAction能获取到book表记录
        	saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");//标记是宽带开户营销活动
        	saleactiveData.put("ORDER_TYPE_CODE", "4800");
        	IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);

        	if(IDataUtil.isNotEmpty(result))
        	{
        		String tradeId = result.getData(0).getString("TRADE_ID","");
            	btd.getMainTradeData().setRsrvStr6(tradeId);//魔百和营销活动流水
            	btd.getMainTradeData().setRsrvStr8(moFee);//魔百和营销活动预存费用
        	}
        }
        //选了魔百和调测费营销活动
        String moProductId2 = mergeWideUserCreateRD.getMoProductId2();
    	String moPackageId2 = mergeWideUserCreateRD.getMoPackageId2();
    	String moFee2 = mergeWideUserCreateRD.getMoFee2();

        if (StringUtils.isNotBlank(moProductId2) && StringUtils.isNotBlank(moPackageId2))
        {
        	IData saleactiveData = new DataMap();
        	saleactiveData.put("SERIAL_NUMBER", serialNumber);
        	saleactiveData.put("PRODUCT_ID", moProductId2);
        	saleactiveData.put("PACKAGE_ID", moPackageId2);
        	saleactiveData.put("ACCEPT_TRADE_ID", acceptTradeId);//用于SaleActiveTrade.java能记录该工单单号到book表，完工时调用DealSaleActiveAction能获取到book表记录
        	saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");//标记是宽带开户营销活动
        	saleactiveData.put("ORDER_TYPE_CODE", "4800");
        	saleactiveData.put("TRADE_TYPE_CODE", "240");
        	saleactiveData.put("X_TRANS_CODE", "SS.SaleActiveRegSVC.tradeReg4Intf");
        	
        	IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);

        	if(IDataUtil.isNotEmpty(result))
        	{
        		String tradeId = result.getData(0).getString("TRADE_ID","");
        		if(null == btd.getMainTradeData().getRsrvStr6() || "".equals(btd.getMainTradeData().getRsrvStr6()))
        		{
                	btd.getMainTradeData().setRsrvStr6(tradeId);//魔百和营销活动流水
        		}
        		else
        		{
                	btd.getMainTradeData().setRsrvStr6(btd.getMainTradeData().getRsrvStr6() + "|" +tradeId);//魔百和营销活动流水
        		}
            	btd.getMainTradeData().setRsrvStr9(moFee2);//魔百和营销活动预存费用

        	}
        }
	}
}
