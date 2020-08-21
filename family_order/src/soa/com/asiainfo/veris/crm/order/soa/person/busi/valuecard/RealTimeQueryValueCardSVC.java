package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;

public class RealTimeQueryValueCardSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
    
    public IDataset queryValueCardInfo(IData inparam) throws Exception{
		String cityCode = inparam.getString("CITY_CODE","");
		if(cityCode.equals(CSBizBean.getVisit().getCityCode()) || CSBizBean.getVisit().getCityCode().startsWith("HNSJ")||CSBizBean.getVisit().getCityCode().startsWith("HNHN")||
				CSBizBean.getVisit().getCityCode().startsWith("HNYD")) {
    	String tradeTypeCode = inparam.getString("TRADE_TYPE_CODE","");
    	String staffIdS = inparam.getString("START_STAFF_ID","");
    	String staffIdE = inparam.getString("END_STAFF_ID","");
    	String startSaleTime = inparam.getString("START_DATE")+SysDateMgr.START_DATE_FOREVER;
    	String endSaleTime = inparam.getString("END_DATE")+SysDateMgr.END_DATE;
    	String resNoS = inparam.getString("X_RES_NO_S","");
    	String resNoE = inparam.getString("X_RES_NO_E","");   
    	IDataset result = TradeFeeDeviceQry.queryValueCardByCondition(tradeTypeCode, cityCode, staffIdS, staffIdE, startSaleTime, endSaleTime, resNoS, resNoE, getPagination());
        if(IDataUtil.isNotEmpty(result)){
        	for (int i = 0,size=result.size(); i < size; i++) {
				IData data = result.getData(i);
				String tempTradeTypeCode = data.getString("TRADE_TYPE_CODE","");
				if(StringUtils.isNotBlank(tempTradeTypeCode)){
					String tradeType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TRADETYPE", 
							new String[]{ "EPARCHY_CODE", "TRADE_TYPE_CODE" }, "TRADE_TYPE",
							new String[]{ CSBizBean.getVisit().getStaffEparchyCode(), tempTradeTypeCode});
					data.put("TRADE_TYPE", tradeType);
				}
				String deviceTypeCode = data.getString("DEVICE_TYPE_CODE","");
				if(StringUtils.isNotBlank(deviceTypeCode)){					
					String kindName = StaticUtil.getStaticValueDataSource(this.getVisit(),"res","RES_SKU",new java.lang.String [] {"MGMT_DISTRICT","RES_SKU_ID"},
							"RES_SKU_NAME",new java.lang.String [] {"ZZZZ",deviceTypeCode});
					data.put("KIND_NAME", kindName);
				}
				
			}
        }
    	return result;
		}
		else {
			return  new DatasetList();
		}
    }
  
   
}
