
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright 2015 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductExpireSVC.java
 * @Description: 代理商开户、批量预开户到期提醒处理服务类 1. 调用营销活动管理接口
 * @version: v1.0.0
 * @author: songlm
 */
public class CreatePersonUserExpireSVC extends CSBizService
{
	
	public IDataset dealExpire(IData mainTrade) throws Exception
	{
		IDataset result = new DatasetList();
		String tradeId = mainTrade.getString("TRADE_ID");

		// 查历史台账 如存在未返销的 才进行处理
		IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

		if (IDataUtil.isNotEmpty(mainHiTrade))
		{
			//办理营销活动
			result = this.regSaleActive(mainTrade);
		}

		return result;
	}

	/**
	 * @Description: 办理营销活动
	 * @param mainTrade
	 * @throws Exception
	 * @author: songlm
	 */
	public IDataset regSaleActive(IData mainTrade) throws Exception
	{
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
		String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
		String tradeCityCode = mainTrade.getString("TRADE_CITY_CODE");
		String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
		String rsrvStr10 = mainTrade.getString("RSRV_STR10", "");
		  
		IDataset result = new DatasetList();

		if (StringUtils.isNotBlank(rsrvStr10))
		{
            String rsrvStrArray[] = rsrvStr10.split("\\|");
	        IDataset commData = CommparaInfoQry.getCommpara("CSM", rsrvStrArray[0], rsrvStrArray[1], tradeEparchyCode);
	        
	        if (IDataUtil.isNotEmpty(commData))
            {
        		String productId = commData.getData(0).getString("PARA_CODE2");
        		String packageId = commData.getData(0).getString("PARA_CODE3");
        		IData param = new DataMap();
        		param.put("SERIAL_NUMBER", serialNumber);
        		param.put("PRODUCT_ID", productId);
        		param.put("PACKAGE_ID", packageId);
                param.put("TRADE_STAFF_ID", tradeStaffId);
	            param.put("TRADE_DEPART_ID", tradeDepartId);
	            param.put("TRADE_CITY_CODE", tradeCityCode);

                IData callData = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param).getData(0);

				IData returnData = new DataMap();
				returnData.clear();
				returnData.put("代理商开户或批量预开户同时受理营销活动:", "ORDER_ID=[" + callData.getString("ORDER_ID", "") + "]," + "TRADE_ID=[" + callData.getString("TRADE_ID", "") + "]");

				result.add(returnData);
            }
		}

		return result;
	}

	

}
